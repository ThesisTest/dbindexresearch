package cn.luoyulingfeng.dbindexresearch.data;

import cn.luoyulingfeng.dbindexresearch.mapper.BookMapper;
import cn.luoyulingfeng.dbindexresearch.model.Book;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BookDataGenerator {


    /**
     * 图书数据生成器
     * @param num
     */
    public static void generate(int num)throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("csvdata/Books.csv"));
        Random random = new Random();
        String[] series = {"计算机丛书", "文学丛书", "软件丛书", "美术丛书", "音乐丛书", "外语丛书"};
        BufferedWriter writer2 = new BufferedWriter(new FileWriter("csvdata/BookSeries.txt"));
        BufferedWriter writer3 = new BufferedWriter(new FileWriter("csvdata/BookPrices.txt"));

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
            book.setBookPrice(formatPrice(random.nextFloat()*100 + 25.6f));
            book.setBookSeries(series[random.nextInt(series.length)]);
            book.setBookCover("/book/cover/" + book.getBookNameCn() + ".jpg");
            book.setBookTotalPage(random.nextInt(748) + 126);
            book.setBookStatus(random.nextInt(2));
            writer.write(book.toString());
            writer.newLine();
            writer2.write(book.getBookSeries());
            writer2.newLine();
            writer3.write(book.getBookPrice()+"");
            writer3.newLine();
        }
        writer.flush();
        writer.close();
        writer2.flush();
        writer2.close();
        writer3.flush();
        writer3.close();
        long end = System.currentTimeMillis();
        System.out.printf("time cost: %fs\n", (end - start)/1e3);
    }

    public static void main(String[] args) throws IOException{
        //generate(10000000);
        List<Float> prices = loadPrices();
        for (int i=0; i<100000; i++){
            if (prices.get(i) == 40.0f){
                System.out.println(i + 1);
            }
        }
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

    public static List<String> loadNames(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader("csvdata/BooksName.txt"));
            String line = null;
            List<String> names = new ArrayList<>(10000000);
            while ((line = reader.readLine()) != null){
                names.add(line);
            }
            reader.close();
            return names;
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static List<String> loadSeries(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader("csvdata/BookSeries.txt"));
            String line = null;
            List<String> names = new ArrayList<>(10000000);
            while ((line = reader.readLine()) != null){
                names.add(line);
            }
            reader.close();
            return names;
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static List<Float> loadPrices(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader("csvdata/BookPrices.txt"));
            String line = null;
            List<Float> prices = new ArrayList<>(10000000);
            while ((line = reader.readLine()) != null){
                prices.add(Float.parseFloat(line));
            }
            reader.close();
            return prices;
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private static float formatPrice(float price){
        DecimalFormat df =new DecimalFormat("#0.0");
        String result = df.format(price);
        return Float.parseFloat(result);
    }
}
