package com.ynjt.core;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

//@formatter:off
/**
 *                             _ooOoo_
 *                            o8888888o
 *                            88" . "88
 *                            (| -_- |)
 *                            O\  =  /O
 *                         ____/`---'\____
 *                       .'  \\|     |//  `.
 *                      /  \\|||  :  |||//  \
 *                     /  _||||| -:- |||||-  \
 *                     |   | \\\  -  /// |   |
 *                     | \_|  ''\---/''  |   |
 *                     \  .-\__  `-`  ___/-. /
 *                   ___`. .'  /--.--\  `. . __
 *                ."" '<  `.___\_<|>_/___.'  >'"".
 *               | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 *               \  \ `-.   \_ __\ /__ _/   .-` /  /
 *          ======`-.____`-.___\_____/___.-`____.-'======
 *                             `=---='
 *          ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 *                     佛祖保佑        永无BUG
 *            佛曰:
 *                   写字楼里写字间，写字间里程序员；
 *                   程序人员写程序，又拿程序换酒钱。
 *                   酒醒只在网上坐，酒醉还来网下眠；
 *                   酒醉酒醒日复日，网上网下年复年。
 *@Author : Gang_Liu
 *@Desc   : 没有框架就撸，拿起键盘就是梭．有Bug你来打我啊
*/
//@formatter:on

public class Property implements Serializable {

    private transient static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //Use static field representing data type, because type
    //byte uses smaller memory than Class
    //this information only needed
    public transient static final byte UNKNOWN = -1;
    public transient static final byte SERIALIZABLE = 0;
    public transient static final byte BYTE = 1;
    public transient static final byte SHORT = 2;
    public transient static final byte INT = 3;
    public transient static final byte LONG = 4;
    public transient static final byte FLOAT = 5;
    public transient static final byte DOUBLE = 6;
    public transient static final byte BOOLEAN = 7;
    public transient static final byte STRING = 8;
    public transient static final byte DATE = 9;
    public transient static final byte NULL = 10;

    private transient static final Map<Class<? extends Number>, Byte> typeMapping = new HashMap<>();
    static{
        typeMapping.put(Byte.class, BYTE);
        typeMapping.put(Short.class, SHORT);
        typeMapping.put(Integer.class, INT);
        typeMapping.put(Long.class, LONG);
        typeMapping.put(Float.class, FLOAT);
        typeMapping.put(Double.class, DOUBLE);
    }

    private final Node node;
    private byte type = NULL;
    private AtomicReference<Serializable> value = new AtomicReference<>();

    //this constructor creates a headless property which is used for virtual nodes
    public Property() {
        this.node = null;
    }

    //this constructor creates a memory based property
    public Property(final Node node) {
        this.node = node;
    }

    public void setValue(final Number value) throws MulValueFormatException {
        final Byte type = typeMapping.get(value.getClass());
        if(type == null){
            throw new MulValueFormatException(value.getClass() + " is not a supported type");
        }
        this.type = type.byteValue();
        this.value.set(value);
    }

    public void setValue(final boolean value) {
        this.value.set(new Boolean(value));
        type = BOOLEAN;
    }

    public void setValue(final long value) {
        this.value.set(new Long(value));
        type = LONG;
    }

    public void setValue(final java.util.Date time) {
        this.value.set(time);
        type = DATE;
    }

    public void setValue(final String value) {
        this.value.set(value);
        type = STRING;
    }

    public void setValue(final Serializable serializable) {
        this.value.set(serializable);
        type = SERIALIZABLE;
    }

    public void setValue(final byte type, final String value) throws MulValueFormatException {
        switch (type) {
            case SHORT:
                this.value.set(Short.parseShort(value));
                this.type = type;
                break;
            case BYTE:
                this.value.set(Byte.parseByte(value));
                this.type = type;
                break;
            case INT:
                this.value.set(Integer.parseInt(value));
                this.type = type;
                break;
            case LONG:
                this.value.set(Long.parseLong(value));
                this.type = type;
                break;
            case FLOAT:
                this.value.set(Float.parseFloat(value));
                this.type = type;
                break;
            case DOUBLE:
                this.value.set(Double.parseDouble(value));
                this.type = type;
                break;
            case STRING:
                this.value.set(value);
                this.type = type;
                break;
            case DATE:
                try {
                    this.value.set(DATE_FORMATTER.parse(value));
                    this.type = type;
                    break;
                } catch (final ParseException ex) {
                    throw new MulValueFormatException(ex);
                }
            case BOOLEAN:
                this.value.set(Boolean.parseBoolean(value));
                this.type = type;
                break;
            default:
                this.type = UNKNOWN;
                throw new MulValueFormatException("Does not support type " + type);
        }
    }

    public String getValueAsString() throws MulValueFormatException {
        switch (type) {
            case SHORT:
            case BYTE:
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
            case BOOLEAN:
                return String.valueOf(this.value.get());
            case STRING:
                return (String) this.value.get();
            case DATE:
                return DATE_FORMATTER.format(this.value.get());
            default:
                throw new MulValueFormatException("Does not support type " + type);
        }
    }

    public Serializable getValue() {
        return this.value.get();
    }

    public byte getType() {
        return type;
    }

    public Node getHost() {
        return node;
    }

    public boolean isHeadless() {
        return node == null;
    }

    public static void main(String[] args) throws MulValueFormatException {
        final Property property = new Property();
        property.setValue(new Integer(2));
        System.out.println(property.getType());
    }

}