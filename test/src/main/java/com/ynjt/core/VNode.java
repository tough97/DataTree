package com.ynjt.core;

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

//TODO figure out how to do this

public abstract class VNode extends Node {

    protected Map<String, SubNodeDependencyMap> subNodeDependencyMap = new ConcurrentHashMap<>();

    protected VNode() throws NodeMisFunctionException {
    }

    protected VNode(final Node parent) throws NodeMisFunctionException {
        super(parent);
    }

    protected VNode(final String id) throws NodeMisFunctionException {
        super(id);
    }

    protected VNode(final Node parent, final String id) throws NodeMisFunctionException {
        super(parent, id);
    }

    //please use subNodeDependencyMap to cache sub node dependencies
    ///the procedure should be
    //1- process the internal logic first
    //2- if first process is done without exception, add or remove cache
    public abstract Set<String> getPropertyKeysFor(final String... subId) throws SubNodeNotFoundException;

    public abstract Property getPropertyFor(final String subId, final String key) throws SubNodeNotFoundException;

    public abstract VNode setPropertyFor(final String subId, final String key, final Serializable value) throws SubNodeNotFoundException;

    public abstract Property removePropertyFor(final String subId, final String key) throws SubNodeNotFoundException;


    public abstract void setDependencyFor(final String subId, final Node host) throws SubNodeNotFoundException;

    public abstract void independentFromHostFor(final String subId, final Node host) throws SubNodeNotFoundException;

    //these two methods are used internally
    protected abstract void removeDependencyFor(final String subId, final Node dependency) throws DependencyOperationException, SubNodeNotFoundException;

    protected abstract void addDependencyFor(final String subId, final Node dependency) throws DependencyOperationException, SubNodeNotFoundException;

    protected static final class SubNodeDependencyMap extends ConcurrentHashMap<String, Node>{
        public SubNodeDependencyMap(){super();}
    }
}
