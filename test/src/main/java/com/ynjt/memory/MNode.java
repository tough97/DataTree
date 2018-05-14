package com.ynjt.memory;

import com.ynjt.core.*;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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

public abstract class MNode extends Node {

    public static final MBranch createRoot() throws NodeMisFunctionException {
        return new MBranch();
    }

    private PropertyListener propertyListener;
    private Map<String, Property> propertyMap = new ConcurrentHashMap<>();

    protected MNode() throws NodeMisFunctionException {
        super();
    }

    protected MNode(final String id) throws NodeMisFunctionException {
        super(id);
    }

    protected MNode(final MNode parent) throws NodeMisFunctionException {
        super(parent);
    }

    protected MNode(final MNode parent, final String id) throws NodeMisFunctionException {
        super(parent, id);
    }

    public PropertyListener getPropertyListener() {
        return propertyListener;
    }

    public MNode setPropertyListener(final PropertyListener propertyListener) {
        this.propertyListener = propertyListener;
        return this;
    }

    /**
     * property manipulations
     */
    public int getPropertyCount() {
        return propertyMap.size();
    }

    public Set<String> getPropertyKeys() {
        return propertyMap.keySet();
    }

    public Property getProperty(final String key) {
        return propertyMap.get(key);
    }

    //returns the previous property associate with this key, or null if this key
    //is used for first time
    public Property setProperty(final String key, final Serializable value) {
        final Property originalValue = propertyMap.get(key);
        final Property newValue = new Property(this);
        newValue.setValue(value);
        if (propertyListener != null) {
            if (originalValue != null) {
                propertyListener.prePropertyRemove(this, originalValue.getValue());
            }
            propertyListener.prePropertySet(this, newValue);
        }

        propertyMap.put(key, newValue);

        if (propertyListener != null) {
            if (originalValue != null) {
                propertyListener.postPropertyRemove(this, originalValue);
            }
            propertyListener.postPropertySet(this, newValue);
        }
        return originalValue;
    }

    public Property removeProperty(final String key) {
        final Property property = propertyMap.get(key);
        if (property != null && propertyListener != null) {
            propertyListener.prePropertyRemove(this, property.getValue());
        }
        propertyMap.remove(key);
        if (property != null && propertyListener != null) {
            propertyListener.postPropertyRemove(this, property.getValue());
        }
        return property;
    }

}
