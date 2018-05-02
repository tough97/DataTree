package com.ynjt.net.test;

import com.sun.xml.internal.messaging.saaj.packaging.mime.util.BASE64DecoderStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.BASE64EncoderStream;

import javax.crypto.*;
import java.net.DatagramPacket;

public class AdhocNodeTest {

    private static final String HELLO_MESSAGE = "Network_Node";

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

    private DatagramPacket udpSocket;

    /**
     * try to 
     */
    private void startAdhocListenerService(){
        
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