package com.ynjt.test;

//@formatter:off
/*
*                     ,----------------,              ,---------,
*                ,-----------------------,          ,"        ,"|
*              ,"                      ,"|        ,"        ,"  |
*             +-----------------------+  |      ,"        ,"    |
*             |  .-----------------.  |  |     +---------+      |
*             |  |                 |  |  |     | -==----'|      |
*             |  |  I LOVE DOS!    |  |  |     |         |      |
*             |  |  Bad command or |  |  |/----|`---=    |      |
*             |  |  C:\>_          |  |  |   ,/|==== ooo |      ;
*             |  |                 |  |  |  // |(((( [33]|    ,"
*             |  `-----------------'  |," .;'| |((((     |  ,"
*             +-----------------------+  ;;  | |         |,"
*                /_)______________(_/  //'   | +---------+
*           ___________________________/___  `,
*          /  oooooooooooooooo  .o.  oooo /,   \,"-----------
*         / ==ooooooooooooooo==.o.  ooo= //   ,`\--{)B     ,"
*        /_==__==========__==_ooo__ooo=_/'   /___________,"
*
*/
//@formatter:on

import com.ynjt.core.Branch;
import com.ynjt.core.DependencyOperationException;
import com.ynjt.core.NodeMisFunctionException;
import com.ynjt.core.UnsupportedException;
import com.ynjt.memory.MBranch;
import com.ynjt.memory.MLeaf;

import java.util.Date;

public class TreeOperationTest {

    public static void main(String[] args) throws NodeMisFunctionException, UnsupportedException, DependencyOperationException {
        final Branch root = MBranch.createRoot();
        final MBranch sub = root.createSubNode(MBranch.class);
        sub.setProperty("H", 1);
        final MLeaf leaf = sub.createSubNode(MLeaf.class);
        leaf.setProperty("J", new Date());

        final MBranch sub1 = root.createSubNode(MBranch.class);
        sub1.setDependencyTo(leaf);

        System.out.println("Here 1");

        sub1.independentFromHost();

        System.out.println("Here 2");
    }

}
