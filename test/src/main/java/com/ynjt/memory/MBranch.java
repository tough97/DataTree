package com.ynjt.memory;

import com.ynjt.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public final class MBranch extends MNode implements Branch {

    private static final Logger logger = LoggerFactory.getLogger(MBranch.class);
    private Map<String, Branch> branchMap = new ConcurrentHashMap<>();
    private Map<String, Leaf> leafMap = new ConcurrentHashMap<>();
    private SubNodeListener subNodeListener = null;

    protected MBranch() throws NodeMisFunctionException {
        super();
    }

    protected MBranch(final String id) throws NodeMisFunctionException {
        super(id);
    }

    protected MBranch(final MNode parent) throws NodeMisFunctionException {
        super(parent);
    }

    protected MBranch(final MNode parent, final String id) throws NodeMisFunctionException {
        super(parent, id);
    }

    @Override
    public <NodeType extends Node> NodeType createSubNode(final Class<NodeType> typeClass) throws UnsupportedException {
        if (!MNode.class.isAssignableFrom(typeClass)) {
            throw new UnsupportedException(typeClass.getCanonicalName() + " can not be supported by " + this.getClass().getCanonicalName());
        }

        try {
            final MNode subNode = Branch.class.isAssignableFrom(typeClass) ? new MBranch(this) :
                    Leaf.class.isAssignableFrom(typeClass) ? new MLeaf(this) : null;
            if (subNode == null) {
                throw new UnsupportedException(typeClass.getCanonicalName() + " is neither Leaf nor Branch");
            }

            if (subNodeListener != null) {
                subNodeListener.preSubNodeCreate(this, subNode);
            }

            if (subNode.getClass().equals(MBranch.class)) {
                branchMap.put(subNode.getId(), (Branch) subNode);
            } else {
                leafMap.put(subNode.getId(), (Leaf) subNode);
            }

            if (subNodeListener != null) {
                subNodeListener.postSubNodeCreate(this, subNode);
            }

            return (NodeType) subNode;
        } catch (final NodeMisFunctionException e) {
            throw new UnsupportedException(e);
        }
    }

    @Override
    public Leaf getSubLeaf(final String id) {
        return leafMap.get(id);
    }

    @Override
    public Branch getSubBranch(final String id) {
        return branchMap.get(id);
    }

    @Override
    public boolean removeSubNode(final String id) {
        final Boolean isSubLeaf = new Boolean(
                leafMap.containsKey(id) ? true :
                        branchMap.containsKey(id) ? false : null
        );
        if(isSubLeaf == null){
            return false;
        }

        final MNode subNode = isSubLeaf ? (MNode) leafMap.get(id) : (MNode) branchMap.get(id);
        if(subNode == null){
            return false;
        }

        if(subNodeListener != null){
            subNodeListener.preSubNodeRemove(this, subNode);
        }

        if(isSubLeaf){
            leafMap.remove(id);
        } else {
            branchMap.remove(id);
        }

        if(subNodeListener != null){
            subNodeListener.postSubNodeCreate(this, subNode);
        }

        return isSubLeaf == null ? false : isSubLeaf;
    }

    @Override
    public void setSubNodeListener(final SubNodeListener subNodeListener) {
        this.subNodeListener = subNodeListener;
    }

    @Override
    public SubNodeListener getSubNodeListener() {
        return subNodeListener;
    }

    @Override
    public int getSubLeafSize() {
        return leafMap.size();
    }

    @Override
    public int getSubBranchSize() {
        return branchMap.size();
    }

    @Override
    public Set<Branch> getSubBranch() {
        return (Set<Branch>) branchMap.values();
    }

    @Override
    public Set<Leaf> getSubLeaf() {
        return (Set<Leaf>) leafMap.values();
    }

    @Override
    public boolean isBranch() {
        return true;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }
}
