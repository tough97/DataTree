package com.ynjt.core;

import com.ynjt.memory.MNode;
import com.ynjt.util.IdGenerator;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
 *
 * Node is the three design principle, it defines how tree components can interact with
 * each other
 *
 * A tree has mainly the following behaviors
 * 1- CURD to sunNodes
 * 2- CURD to properties
 * 3- CURD to dependencies
 *
*/
//@formatter:on

public abstract class Node implements Serializable {

    //static members
    private static IdGenerator idGenerator = null;

    public static void setIdGenerator(final IdGenerator idGenerator) {
        Node.idGenerator = idGenerator;
    }

    private final Node parent;
    private final String id;
    private String name;
    private Set<Node> dependencies = new HashSet<>();
    private PropertyListener propertyListener = null;

    //check this out, since node dependencies are designed to be one-to-many, so there will be always
    //at most one node to reference.
    //for dependencies in memory node we can use a Set<Node> or a Map<id[String], Set<Node>> in virtual node
    private Node host;

    //invoke them from dependent perspective
    private DependencyListener dependencyListener;

    protected Node() throws NodeMisFunctionException {
        parent = null;
        id = idGenerator == null ? UUID.randomUUID().toString() : idGenerator.generateNewId();
        nodeFunctionalChecking();
    }

    protected Node(final Node parent) throws NodeMisFunctionException {
        this.parent = parent;
        id = idGenerator == null ? UUID.randomUUID().toString() : idGenerator.generateNewId();
        nodeFunctionalChecking();
    }

    protected Node(final String id) throws NodeMisFunctionException {
        this(null, id);
    }

    //this constructor should be used to when sub-class needs copy node
    //from one form to another
    protected Node(final Node parent, final String id) throws NodeMisFunctionException {
        this.parent = parent;
        this.id = id;
        nodeFunctionalChecking();
    }

    private void nodeFunctionalChecking() throws NodeMisFunctionException {
        
        final boolean leafFound = Leaf.class.isAssignableFrom(this.getClass());
        final boolean branchFound = Branch.class.isAssignableFrom(this.getClass());
        
        if (branchFound == leafFound) {
            throw new NodeMisFunctionException(branchFound ?
                    (this.getClass().getCanonicalName() + " implements Branch and Leaf at same time") :
                    (this.getClass().getCanonicalName() + " should implement at least one Branch or Leaf")
            );
        }
    }

    public DependencyListener getDependencyListener() {
        return dependencyListener;
    }

    public Node setDependencyListener(final DependencyListener dependencyListener) {
        this.dependencyListener = dependencyListener;
        return this;
    }

    public Node getParent() {
        return parent;
    }

    public int getDepth() {
        return parent == null ? 0 : (parent.getDepth() + 1);
    }

    public boolean isRoot() {
        return parent == null;
    }

    public Node getRoot(){
        if(isRoot()){
            return parent;
        }

        Node ret = parent;
        while(!ret.isRoot()){
            ret = ret.getParent();
        }
        return ret;
    }

    //add dependency form dependent
    public Node setDependencyTo(final Node host) throws DependencyOperationException {
        if (this.host != null) {
            this.host.removeDependencyFor(this);
        }

        if (dependencyListener != null) {
            dependencyListener.preDependencyAdd(host, this);
        }
        this.host = host;
        host.addDependencyFor(this);
        if (dependencyListener != null) {
            dependencyListener.postDependencyAdd(host, this);
        }

        return this;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Node setName(String name) {
        this.name = name;
        return this;
    }

    //remove dependency from dependent
    public Node independentFromHost() throws DependencyOperationException {
        if (host == null) {
            return this;
        }
        if (dependencyListener != null) {
            dependencyListener.preDependencyRemoved(host, this);
        }

        host.removeDependencyFor(this);

        if (dependencyListener != null) {
            dependencyListener.postDependencyRemoved(host, this);
        }
        return this;
    }

    public Node getHost() {
        return host;
    }
    
    protected void removeDependencyFor(final Node dependency) throws DependencyOperationException {
        if (dependency.getHost() != this) {
            throw new DependencyOperationException("Dependency of " + dependency.toString() + " is not " + this.toString());
        }
        if(!dependencies.remove(dependency)){
            throw new DependencyOperationException("Bad dependency link, " + this.toString() + " is the host according to " + dependency.toString());
        }
    }
    
    protected void addDependencyFor(final Node dependency) throws DependencyOperationException {
        dependencies.add(dependency);
    }

    //abstract methods
    //utility methods
    public abstract boolean isBranch();

    public abstract boolean isLeaf();

    /**
     * property methods
     */
    /**
     * property manipulations
     */
    public PropertyListener getPropertyListener(){
        return propertyListener;
    }

    public Node setPropertyListener(final PropertyListener propertyListener){
        this.propertyListener = propertyListener;
        return this;
    }

    public abstract int getPropertyCount();

    public abstract Set<String> getPropertyKeys();

    public abstract Property getProperty(final String key);

    //returns the previous property associate with this key, or null if this key
    //is used for first time
    public abstract Property setProperty(final String key, final Serializable value);

    public abstract Property removeProperty(final String key);

    public String toString(){
        return this.getClass().getSimpleName() + "[" + id + "]";
    }

}