package cn.luoyulingfeng.dbindexresearch.index;

import cn.luoyulingfeng.dbindexresearch.data.BookBorrowDataGenerator;
import cn.luoyulingfeng.dbindexresearch.data.BookDataGenerator;
import cn.luoyulingfeng.dbindexresearch.data.StudentDataGenerator;
import cn.luoyulingfeng.dbindexresearch.model.Book;
import cn.luoyulingfeng.dbindexresearch.model.BookBorrowRecord;
import cn.luoyulingfeng.dbindexresearch.model.Student;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.assertj.core.util.DateUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.sql.Date;
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
        long start = System.currentTimeMillis();
        FileReader reader = new FileReader("csvdata/Books.csv");
        CSVParser csvParser = CSVFormat.DEFAULT.parse(reader);

        List<Book> books = new ArrayList<>();
        int cnt = 0;
        for (CSVRecord record: csvParser){
            if (cnt > 1000000){
                break;
            }
            Book book = new Book();
            book.setId(Integer.parseInt(record.get(0)));
            book.setBookIsbn(record.get(1));
            book.setBookBookcaseNo(record.get(2));
            book.setBookNameCn(record.get(3));
            book.setBookNameEn(record.get(4));
            book.setBookAuthor(record.get(5));
            book.setBookPress(record.get(6));
            book.setBookVersion(Integer.parseInt(record.get(7)));
            book.setBookPrice(Float.parseFloat(record.get(8)));
            book.setBookSeries(record.get(9));
            book.setBookCover(record.get(10));
            book.setBookTotalPage(Integer.parseInt(record.get(11)));
            book.setBookStatus(Integer.parseInt(record.get(12)));
            books.add(book);
            cnt++;
        }
        long end = System.currentTimeMillis();
        System.out.printf("time cost %f s\n", (end - start)/1e3);
        csvParser.close();
        for (int i=0; i<5; i++){
            System.out.println(books.get(i));
        }
    }
}
