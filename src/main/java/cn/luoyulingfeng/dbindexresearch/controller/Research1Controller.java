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

@SuppressWarnings("Duplicates")
@RestController
@RequestMapping(value = "/research1")
public class Research1Controller {

    private LinearHash linearHash = null;
    private ExtendibleHash extendibleHash = null;
    private BPlusTree bPlusTree = null;
    private static List<String> bookNameList = null;
    static {
        bookNameList = BookDataGenerator.loadNames();
        System.out.println("BookNameList Size:" + bookNameList.size());
    }

    @PostMapping(value = "/insert")
    public JsonResult insert(@RequestParam int num){
        linearHash = new LinearHash();
        extendibleHash = new ExtendibleHash();
        bPlusTree = new BPlusTree(100);
        InsertResult result = new InsertResult();

        long start, end;
        start = System.currentTimeMillis();
        for (int i=0; i<num; i++){
            linearHash.insert(bookNameList.get(i), i);
        }
        end = System.currentTimeMillis();
        result.linearHashTime = (end - start)/1f;

        start = System.currentTimeMillis();
        for (int i=0; i<num; i++){
            extendibleHash.insert(bookNameList.get(i), i);
        }
        end = System.currentTimeMillis();
        result.extendibleHashTime = (end - start)/1f;

        start = System.currentTimeMillis();
        for (int i=0; i<num; i++){
            bPlusTree.insert(bookNameList.get(i), i);
        }
        end = System.currentTimeMillis();
        result.bPlusTreeTime = (end - start)/1f;

        return new InfoJsonResult(0, "success", result);
    }

    @PostMapping(value = "/search")
    public JsonResult search(@RequestParam String bookName){
        SearchResult result = new SearchResult();
        long start, end;

        start = System.currentTimeMillis();
        result.linearHashResults = linearHash.find(bookName);
        end = System.currentTimeMillis();
        result.linearHashTime = (end - start)/1f;

        start = System.currentTimeMillis();
        result.extendibleHashResults = extendibleHash.find(bookName);
        end = System.currentTimeMillis();
        result.extendibleHashTime = (end - start)/1f;

        start = System.currentTimeMillis();
        result.bPlusTreeResults = bPlusTree.find(bookName);
        end = System.currentTimeMillis();
        result.bPlusTreeTime = (end - start)/1f;

        return new InfoJsonResult(0, "success", result);
    }

    @PostMapping(value = "/delete")
    public JsonResult delete(@RequestParam int num){
        DeleteResult result = new DeleteResult();

        long start, end;
        start = System.currentTimeMillis();
        for (int i=0; i<num; i++){
            linearHash.delete(bookNameList.get(i));
        }
        end = System.currentTimeMillis();
        result.linearHashTime = (end - start)/1f;

        start = System.currentTimeMillis();
        for (int i=0; i<num; i++){
            extendibleHash.delete(bookNameList.get(i));
        }
        end = System.currentTimeMillis();
        result.extendibleHashTime = (end - start)/1f;

        start = System.currentTimeMillis();
        for (int i=0; i<num; i++){
            bPlusTree.delete(bookNameList.get(i));
        }
        end = System.currentTimeMillis();
        result.bPlusTreeTime = (end - start)/1f;

        return new InfoJsonResult(0, "success", result);
    }


    private static class InsertResult{
        public float linearHashTime;
        public float extendibleHashTime;
        public float bPlusTreeTime;
    }

    private static class SearchResult{
        public float linearHashTime;
        public float extendibleHashTime;
        public float bPlusTreeTime;
        public List<Object> linearHashResults;
        public List<Object> extendibleHashResults;
        public List<Object> bPlusTreeResults;
    }

    private static class DeleteResult{
        public float linearHashTime;
        public float extendibleHashTime;
        public float bPlusTreeTime;
    }
}
