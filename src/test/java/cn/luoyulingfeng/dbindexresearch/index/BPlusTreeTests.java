package cn.luoyulingfeng.dbindexresearch.index;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class BPlusTreeTests {

    public static BPlusTree tree;

    @BeforeAll
    public static void init(){
        tree = new BPlusTree(5);
    }

    @Test
    public void testInsert(){
        int[] datas = new int[]{10, 16, 15, 17, 18, 20, 28 ,26, 33, 35, 38, 36, 47, 47, 50};
        for(int data: datas){
            System.out.println("--------------"+data);
            tree.insert(data+"", data);
            tree.printBtree(tree.getRoot());
        }

        System.out.println(tree.find("16", "48"));
    }

    @Test
    public void testInsertStudent(){

    }
}
