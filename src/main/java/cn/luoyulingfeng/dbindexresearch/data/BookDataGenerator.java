package cn.luoyulingfeng.dbindexresearch.data;

import cn.luoyulingfeng.dbindexresearch.mapper.BookMapper;
import cn.luoyulingfeng.dbindexresearch.model.Book;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BookDataGenerator {


    /**
     * 图书数据生成器
     * @param num
     */
    public static void generate(int num)throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("Books.csv"));
        Random random = new Random();
        String[] series = {"计算机丛书", "文学丛书", "软件丛书", "美术丛书", "音乐丛书", "外语丛书"};

        long start = System.currentTimeMillis();
        int cnt = 0;
        for (int i=0; i<num; i++){
            cnt++;
            System.out.printf("%.2f%%\n", (cnt*1f / num) * 100);

            Book book = new Book();
            book.setId(i + 1);
            book.setBookIsbn(String.format("%d-%d-%d-%d-%d", random.nextInt(1000), random.nextInt(10), random.nextInt(1000), random.nextInt(100000), random.nextInt(10)));
            book.setBookBookcaseNo(String.format("bookcase%d-%d", random.nextInt(1000), random.nextInt(100)));
            book.setBookNameCn("图书" + i);
            book.setBookNameEn("book" + i);
            book.setBookAuthor("author" + random.nextInt(100000));
            book.setBookPress(random.nextInt(10000) + "出版社");
            book.setBookVersion(random.nextInt(5) + 1);
            book.setBookPrice(random.nextFloat()*100 + 25.6f);
            book.setBookSeries(series[random.nextInt(series.length)]);
            book.setBookCover("/book/cover/" + book.getBookNameCn() + ".jpg");
            book.setBookTotalPage(random.nextInt(748) + 126);
            book.setBookStatus(random.nextInt(2));
            writer.write(book.toString());
            writer.newLine();
        }
        writer.flush();
        writer.close();
        long end = System.currentTimeMillis();
        System.out.printf("time cost: %fs\n", (end - start)/1e3);
    }

    public static void main(String[] args) throws IOException{
        generate(10000000);
    }

    public static List<Book> load(String file)throws IOException{
        List<Book> books = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        while((line = reader.readLine()) != null){
            String[] info = line.split(",");
            Book book = new Book();
            book.setId(Integer.parseInt(info[0]));
            book.setBookIsbn(info[1]);
            book.setBookBookcaseNo(info[2]);
            book.setBookNameCn(info[3]);
            book.setBookNameEn(info[4]);
            book.setBookAuthor(info[5]);
            book.setBookPress(info[6]);
            book.setBookVersion(Integer.parseInt(info[7]));
            book.setBookPrice(Float.parseFloat(info[8]));
            book.setBookSeries(info[9]);
            book.setBookCover(info[10]);
            book.setBookTotalPage(Integer.parseInt(info[11]));
            book.setBookStatus(Integer.parseInt(info[12]));
            books.add(book);
        }
        reader.close();
        return books;
    }
}
