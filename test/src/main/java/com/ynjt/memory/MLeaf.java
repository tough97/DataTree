package com.ynjt.memory;

import com.ynjt.core.Leaf;
import com.ynjt.core.NodeMisFunctionException;

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

public final class MLeaf extends MNode implements Leaf {

    protected MLeaf() throws NodeMisFunctionException {
    }

    protected MLeaf(final String id) throws NodeMisFunctionException {
        super(id);
    }

    protected MLeaf(final MNode parent) throws NodeMisFunctionException {
        super(parent);
    }

    protected MLeaf(final MNode parent, final String id) throws NodeMisFunctionException {
        super(parent, id);
    }

    @Override
    public boolean isBranch() {
        return false;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }
}
