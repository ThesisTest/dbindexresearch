package cn.luoyulingfeng.dbindexresearch.index;

import cn.luoyulingfeng.dbindexresearch.data.BookBorrowDataGenerator;
import cn.luoyulingfeng.dbindexresearch.data.BookDataGenerator;
import cn.luoyulingfeng.dbindexresearch.data.StudentDataGenerator;
import cn.luoyulingfeng.dbindexresearch.model.Book;
import cn.luoyulingfeng.dbindexresearch.model.BookBorrowRecord;
import cn.luoyulingfeng.dbindexresearch.model.Student;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LinearHashTests {

    private static LinearHash linearHash;

    @BeforeAll
    public static void init(){
        linearHash = new LinearHash();
    }


    @Test
    public void testInsertStudent()throws IOException {
        List<Student> students = StudentDataGenerator.load("csvdata/Students.csv");
        long start = System.currentTimeMillis();
        for (int i=0; i<students.size(); i++){
            Student student = students.get(i);
            linearHash.insert(student.getStuNo(), i);
        }
        long end = System.currentTimeMillis();
        System.out.printf("time cost: %fms\n", (end - start)/1f);
        String structure = linearHash.structure();
        System.out.println(structure);
    }

    @Test
    public void testInsertBookBorrow()throws IOException {
        List<BookBorrowRecord> records = BookBorrowDataGenerator.load("csvdata/BookBorrowRecords.csv");
        long start = System.currentTimeMillis();
        for (int i=0; i<records.size(); i++){
            BookBorrowRecord record = records.get(i);
            linearHash.insert(record.getPersonNo(), i);
        }
        long end = System.currentTimeMillis();
        System.out.printf("time cost: %fms\n", (end - start)/1f);
        String structure = linearHash.structure();
        System.out.println(structure);
    }

    @Test
    public void testInsertBook()throws IOException {
        List<Book> books = BookDataGenerator.load("csvdata/Books.csv");
        long start = System.currentTimeMillis();
        int cnt = 0;
        int size = books.size();
        for (int i=0; i<books.size(); i++){
            Book book = books.get(i);

            cnt++;
            System.out.printf("%.2f%%\n", cnt*1f/size*100);
            linearHash.insert(book.getBookIsbn(), i);
        }
        long end = System.currentTimeMillis();
        System.out.printf("time cost: %fms\n", (end - start)/1f);
        String structure = linearHash.structure();
        System.out.println(structure);
    }

    @Test
    public void testReadAllLines()throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader("csvdata/BooksName.txt"));
        String line = null;
        List<String> names = new ArrayList<>(10000000);
        while ((line = reader.readLine()) != null){
            names.add(line);
        }
        reader.close();
    }
}
