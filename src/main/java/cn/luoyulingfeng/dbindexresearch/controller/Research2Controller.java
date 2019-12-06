package cn.luoyulingfeng.dbindexresearch.controller;

import cn.luoyulingfeng.dbindexresearch.common.InfoJsonResult;
import cn.luoyulingfeng.dbindexresearch.common.JsonResult;
import cn.luoyulingfeng.dbindexresearch.data.BookDataGenerator;
import cn.luoyulingfeng.dbindexresearch.index.BPlusTree;
import cn.luoyulingfeng.dbindexresearch.index.ExtendibleHash;
import cn.luoyulingfeng.dbindexresearch.index.LinearHash;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/research2")
public class Research2Controller {

    private LinearHash linearHash1 = new LinearHash();
    private LinearHash linearHash2 = new LinearHash();
    private ExtendibleHash extendibleHash1 = new ExtendibleHash();
    private ExtendibleHash extendibleHash2 = new ExtendibleHash();
    private BPlusTree bPlusTree1 = new BPlusTree(100);
    private BPlusTree bPlusTree2 = new BPlusTree(100);
    private static List<String> bookNameList = null;
    private static List<String> bookSeriesList = null;

    private int insertIndex = 0;

    static {
        bookNameList = BookDataGenerator.loadNames();
        System.out.println("BookNameList Size:" + bookNameList.size());
        bookSeriesList = BookDataGenerator.loadSeries();
        System.out.println("BookSeriesList Size:" + bookSeriesList.size());
    }

    @PostMapping(value = "/insert")
    public JsonResult insert(@RequestParam int num){
        System.out.println("/research2/insert");
        System.out.println("num: " + num);

        InsertResult result = new InsertResult();
        long start, end;

        start = System.nanoTime();
        for (int i=insertIndex; i<num + insertIndex; i++){
            linearHash1.insert(bookNameList.get(i), i + 1);
        }
        end = System.nanoTime();
        result.linearHash1Time = (end - start)/1f;

        start = System.nanoTime();
        for (int i=insertIndex; i<num + insertIndex; i++){
            linearHash2.insert(bookSeriesList.get(i), i + 1);
        }
        end = System.nanoTime();
        result.linearHash2Time = (end - start)/1f;

        start = System.nanoTime();
        for (int i=insertIndex; i<num + insertIndex; i++){
            extendibleHash1.insert(bookNameList.get(i), i + 1);
        }
        end = System.nanoTime();
        result.extensibleHash1Time = (end - start)/1f;

        start = System.nanoTime();
        for (int i=insertIndex; i<num + insertIndex; i++){
            extendibleHash2.insert(bookSeriesList.get(i), i + 1);
        }
        end = System.nanoTime();
        result.extensibleHash2Time = (end - start)/1f;

        start = System.nanoTime();
        for (int i=insertIndex; i<num + insertIndex; i++){
            bPlusTree1.insert(bookNameList.get(i), i + 1);
        }
        end = System.nanoTime();
        result.bPlusTree1Time = (end - start)/1f;

        start = System.nanoTime();
        for (int i=insertIndex; i<num + insertIndex; i++){
            bPlusTree2.insert(bookSeriesList.get(i), i + 1);
        }
        end = System.nanoTime();
        result.bPlusTree2Time = (end - start)/1f;

        insertIndex += num;
        System.out.println("exit");
        return new InfoJsonResult(0, "success", result);
    }


    private static class InsertResult{
        public float linearHash1Time;
        public float linearHash2Time;
        public float extensibleHash1Time;
        public float extensibleHash2Time;
        public float bPlusTree1Time;
        public float bPlusTree2Time;
    }
}
