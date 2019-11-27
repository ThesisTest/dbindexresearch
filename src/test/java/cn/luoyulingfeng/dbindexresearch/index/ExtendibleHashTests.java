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
import java.util.List;

public class ExtendibleHashTests {

    private static ExtendibleHash extendibleHash;

    @BeforeAll
    public static void init(){
        extendibleHash = new ExtendibleHash();
    }

    @Test
    public void testInsert(){
        Integer[] ids = {16, 4, 6, 22, 24, 10, 31, 7, 9, 20, 26};
        for (int id: ids){
            extendibleHash.insert(id+"", id);
        }
        System.out.println(extendibleHash.structure());
    }

    @Test
    public void testInsertStudent()throws IOException {
        List<Student> students = StudentDataGenerator.load("csvdata/Students.csv");
        long start = System.currentTimeMillis();
        for (int i=0; i<students.size(); i++){
            Student student = students.get(i);
            extendibleHash.insert(student.getStuAge()+"", i);
        }
        long end = System.currentTimeMillis();
        System.out.printf("time cost: %fms\n", (end - start)/1f);
        String structure = extendibleHash.structure();
        System.out.println(structure);

        List<Object> result = extendibleHash.find("20");
        System.out.println(result);
    }

    @Test
    public void testInsertBookBorrow()throws IOException {
        List<BookBorrowRecord> records = BookBorrowDataGenerator.load("csvdata/BookBorrowRecords.csv");
        long start = System.currentTimeMillis();
        for (int i=0; i<records.size(); i++){
            BookBorrowRecord record = records.get(i);
            extendibleHash.insert(record.getPersonNo(), i);
        }
        long end = System.currentTimeMillis();
        System.out.printf("time cost: %fms\n", (end - start)/1f);
        String structure = extendibleHash.structure();
        System.out.println(structure);

        List<Object> result = extendibleHash.find("154987534");
        System.out.println(result);
    }

    @Test
    public void testInsertBook()throws IOException{
        List<Book> books = BookDataGenerator.load("csvdata/Books.csv");
        long start = System.currentTimeMillis();
        int cnt = 0;
        int size = books.size();
        for (int i=0; i<books.size(); i++){
            Book book = books.get(i);

            cnt++;
            System.out.printf("%.2f%%\n", cnt*1f/size*100);
            extendibleHash.insert(book.getBookIsbn(), i);
        }
        long end = System.currentTimeMillis();
        System.out.printf("time cost: %fms\n", (end - start)/1f);
        String structure = extendibleHash.structure();
        System.out.println(structure);
    }

}
