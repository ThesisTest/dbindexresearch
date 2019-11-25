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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class LinearHashTests {

    private static LinearHash linearHash;

    @BeforeAll
    public static void init(){
        linearHash = new LinearHash();
    }


    @Test
    public void testInsertStudent()throws IOException {
        List<Student> students = StudentDataGenerator.load("data/Students.csv");
        long start = System.currentTimeMillis();
        for (Student student: students){
            linearHash.insert(student.getStuNo(), student);
        }
        long end = System.currentTimeMillis();
        System.out.printf("time cost: %fms\n", (end - start)/1f);
        String res = JSONObject.toJSONString(linearHash);
        PrintWriter writer = new PrintWriter("data/linear_hash_student.json");
        writer.write(res);
        writer.flush();
        writer.close();
    }

    @Test
    public void testInsertBookBorrow()throws IOException {
        List<BookBorrowRecord> records = BookBorrowDataGenerator.load("data/BookBorrowRecords.csv");
        long start = System.currentTimeMillis();
        for (BookBorrowRecord record: records){
            linearHash.insert(record.getPersonNo()+record.getPersonType()+record.getBookId(), record);
        }
        long end = System.currentTimeMillis();
        System.out.printf("time cost: %fms\n", (end - start)/1f);
        String res = JSONObject.toJSONString(linearHash);
        PrintWriter writer = new PrintWriter("data/linear_hash_book_borrow.json");
        writer.write(res);
        writer.flush();
        writer.close();
    }

    @Test
    public void testInsertBook()throws IOException {
        List<Book> books = BookDataGenerator.load("data/Books.csv");
        long start = System.currentTimeMillis();
        int cnt = 0;
        int size = books.size();
        for (Book book: books){
            cnt++;
            System.out.printf("%.2f%%\n", cnt*1f/size*100);

            linearHash.insert(book.getBookIsbn(), book);
        }
        long end = System.currentTimeMillis();
        System.out.printf("time cost: %fms\n", (end - start)/1f);
        String res = JSONObject.toJSONString(linearHash);
        PrintWriter writer = new PrintWriter("data/linear_hash_book.json");
        writer.write(res);
        writer.flush();
        writer.close();
    }
}
