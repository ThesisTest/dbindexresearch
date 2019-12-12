package cn.luoyulingfeng.dbindexresearch.index;

import cn.luoyulingfeng.dbindexresearch.data.BookDataGenerator;
import cn.luoyulingfeng.dbindexresearch.data.StudentDataGenerator;
import cn.luoyulingfeng.dbindexresearch.model.Student;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

public class BPlusTreeTests {

    public static BPlusTree tree;

    @BeforeAll
    public static void init(){
        tree = new BPlusTree(4);
    }

    @Test
    public void testInsert(){
        int[] datas = new int[]{10, 16, 15, 17, 18, 20, 28 ,26, 33, 35, 38, 36, 47, 47, 50};
        for(int data: datas){
            System.out.println("--------------"+data);
            tree.insert(data+"", data);
            tree.printBtree(tree.getRoot());
        }
    }

    @Test
    public void testInsertStudent()throws IOException {
        List<Student> students = StudentDataGenerator.load("csvdata/Students.csv");
        long start = System.currentTimeMillis();
        for (int i=0; i<students.size(); i++){
            Student student = students.get(i);
            tree.insert(student.getStuNo(), i);
        }
        long end = System.currentTimeMillis();
        System.out.printf("time cost: %fms\n", (end - start)/1f);
        tree.printBtree(tree.getRoot());
    }

    @Test
    public void testFind(){
        List<String> bookNameList = null;
        bookNameList = BookDataGenerator.loadNames();
        for (int i=0; i<10; i++){
            tree.insert(bookNameList.get(i), i + 1);
        }
        List<Object> results = tree.find("图书1000");
        System.out.println(results);
    }
}
