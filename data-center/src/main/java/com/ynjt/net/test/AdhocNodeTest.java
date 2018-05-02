package com.ynjt.net.test;

import com.sun.xml.internal.messaging.saaj.packaging.mime.util.BASE64DecoderStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.BASE64EncoderStream;

import javax.crypto.*;
import javax.jnlp.IntegrationService;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Tutorial for adhoc came from https://demey.io/network-discovery-using-udp-broadcast/
 * author: gnag_liu
 * this is testing code, if anything went wrong when you use it, came and kick me
 */

public class AdhocNodeTest {

    public static final int UDP_PORT = 8899;
    public static final int TCP_PORT = 8889;

    private static final int MAX_UDP_LENGTH = 1024 * 50;

    //the final message should be Network_node:[xxxx] where xxxx is the port used by tcp
    private static final String HANDSHAKING_MESSAGE = "Who's_your_daddy_and_what_does_he_do";

    private static Cipher ecipher;
    private static Cipher dcipher;
    private static SecretKey key;

    static {
        try {
            key = KeyGenerator.getInstance("DES").generateKey();
            ecipher = Cipher.getInstance("DES");
            dcipher = Cipher.getInstance("DES");

            // initialize the ciphers with the given key
            ecipher.init(Cipher.ENCRYPT_MODE, key);
            dcipher.init(Cipher.DECRYPT_MODE, key);
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String encrypt(final String str) {

        try {
            // encode the string into a sequence of bytes using the named charset
            // storing the result into a new byte array.
            final byte[] utf8 = str.getBytes("UTF8");
            byte[] enc = ecipher.doFinal(utf8);
            // encode to base64
            enc = BASE64EncoderStream.encode(enc);
            return new String(enc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decrypt(final String str) {
        try {
            // decode with base64 to get bytes
            final byte[] dec = BASE64DecoderStream.decode(str.getBytes());
            final byte[] utf8 = dcipher.doFinal(dec);
            // create new string based on the specified charset
            return new String(utf8, "UTF8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //static members above-----------------------------------------------------------

    private Set<SocketChannel> servers = new HashSet<>();
    private Set<SocketChannel> clients = new HashSet<>();
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private ExecutorService boardService;
    private ExecutorService tcpService;

    private void hello(){
        System.out.println("Hello");
    }
     
    private void connectToMessageBroadeCast(final String ip, final int port) throws IOException {
        final SocketChannel partnerConnection = SocketChannel.open(new InetSocketAddress(ip, port));
        final ByteBuffer byteBuffer = ByteBuffer.wrap(String.valueOf(TCP_PORT).getBytes());
        partnerConnection.write(byteBuffer);
        servers.add(partnerConnection);
    }

    public AdhocNodeTest startTCPConnectionService() throws IOException {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress("localhost", TCP_PORT));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        tcpService = Executors.newSingleThreadExecutor();
        tcpService.submit(()->{
            while(true){
                selector.select();
                final Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                while(iter.hasNext()){
                    final SelectionKey key = iter.next();
                    //this connection came from nodes received UDP handshaking message
                    if(key.isAcceptable()){
                        final SocketChannel clientSocket = serverSocketChannel.accept();
                        clientSocket.configureBlocking(false);
                        clientSocket.register(selector, SelectionKey.OP_READ);
                        clients.add(clientSocket);
                    }

                    //when connected a port from remote
                    if(key.isReadable()){
                       final SocketChannel socketChannel = (SocketChannel) key.channel();
                        final ByteBuffer byteBuffer = ByteBuffer.allocate(MAX_UDP_LENGTH);
                        socketChannel.read(byteBuffer);
                        final String port = new String(byteBuffer.array());
                        SocketChannel.open(new InetSocketAddress((SocketChannel) key.channel()).socket().getInetAddress().toString())
                    }
                    iter.remove();
                }
            }
        });
        return this;
    }

    //when any new node broader cast message is received, we verify message and connect to given node
    //accordingly, so there will be no need to respond verified UDP message
    public AdhocNodeTest startUDPBoraderCastService() throws UnknownHostException, SocketException {
        final DatagramSocket udpSocket = new DatagramSocket(UDP_PORT, InetAddress.getByName("0.0.0.0"));
        udpSocket.setBroadcast(true);

        boardService = Executors.newSingleThreadExecutor();
        boardService.submit(()->{
            while(true) {
                //waiting to receive package sent by broadcasting new nodes
                final byte[] receivedBuffer = new byte[MAX_UDP_LENGTH];
                final DatagramPacket receivedPackage = new DatagramPacket(receivedBuffer, receivedBuffer.length);
                udpSocket.receive(receivedPackage);

                //here we received a message then we check if the client message is an handshake
                final StringBuilder receivedMessageLog = new StringBuilder("Received data form ")
                        .append(receivedPackage.getAddress().getHostAddress());
                final String handShakingMessage = decrypt(new String(receivedPackage.getData()));

                if(handShakingMessage.isEmpty() || !handShakingMessage.contains(":")){
                    receivedMessageLog.append("\n But it is not an candidate node");
                    continue;
                }

                final String[] handShakingElement = handShakingMessage.split(":");
                if(handShakingElement[0].equals(HANDSHAKING_MESSAGE)){
                    connectToMessageBroadeCast(receivedPackage.getAddress().getHostAddress(), Integer.parseInt(handShakingElement[1]));
                    receivedMessageLog.append("\nAnd the message is hand-shacking");
                } else {
                    receivedMessageLog.append("\n But it is not an candidate node");
                }
                                                
            }
        });
        return this;
    }


    public static void main(String[] args) {
        final String message = "when you tell me story what ever, everyday situations they start to simplify";
        final String encMessage = encrypt(message);
        System.out.println(encMessage);
        final String decMessage = decrypt(encMessage);
        System.out.println(decMessage);
        System.out.println("decMessage.equals(message) = " + decMessage.equals(message));
    }

}