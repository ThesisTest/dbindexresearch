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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping(value = "/research3")
public class Research3Controller {
    private static LinearHash linearHash = null;
    private static ExtendibleHash extendibleHash = null;
    private static BPlusTree bPlusTree = null;
    private static List<Float> bookPriceList = null;
    static {
        bookPriceList = BookDataGenerator.loadPrices();
        System.out.println("BookPriceList Size:" + bookPriceList.size());
    }

    @PostMapping(value = "/init")
    public JsonResult init(@RequestParam int num){
        linearHash = new LinearHash();
        extendibleHash = new ExtendibleHash();
        bPlusTree = new BPlusTree(100);
        for (int i=0; i<num; i++){
            linearHash.insert(bookPriceList.get(i)+"", i + 1);
            extendibleHash.insert(bookPriceList.get(i)+"", i + 1);
            bPlusTree.insert(bookPriceList.get(i)+"", i + 1);
        }
        return new JsonResult(0, "success");
    }

    @PostMapping(value = "/equalSearch")
    public JsonResult equalSearch(@RequestParam String bookPrice){
        SearchResult result = new SearchResult();
        List<Object> list1 = linearHash.find(bookPrice);
        result.linearHashResults = new ArrayList<>();
        for (int i=0; i<list1.size(); i++){
            result.linearHashResults.add((int)list1.get(i));
        }
        Collections.sort(result.linearHashResults);

        List<Object> list2 = extendibleHash.find(bookPrice);
        result.extensibleHashResults = new ArrayList<>();
        for (int i=0; i<list2.size(); i++){
            result.extensibleHashResults.add((int)list2.get(i));
        }
        Collections.sort(result.extensibleHashResults);

        List<Object> list3 = bPlusTree.find(bookPrice);
        result.bPlusTreeResults = new ArrayList<>();
        for (int i=0; i<list3.size(); i++){
            result.bPlusTreeResults.add((int)list3.get(i));
        }
        Collections.sort(result.bPlusTreeResults);

        return new InfoJsonResult(0, "success", result);
    }

    @PostMapping(value = "/scopeSearch")
    public JsonResult scopeSearch(@RequestParam String bookPrice1,
                                  @RequestParam String bookPrice2){
        SearchResult result = new SearchResult();
        result.linearHashResults = new ArrayList<>();
        result.extensibleHashResults = new ArrayList<>();
        List<Object> list3 = bPlusTree.find(bookPrice1, bookPrice2);
        result.bPlusTreeResults = new ArrayList<>();
        for (int i=0; i<list3.size(); i++){
            result.bPlusTreeResults.add((int)list3.get(i));
        }
        Collections.sort(result.bPlusTreeResults);

        return new InfoJsonResult(0, "success", result);
    }

    private static class SearchResult{
        public List<Integer> linearHashResults;
        public List<Integer> extensibleHashResults;
        public List<Integer> bPlusTreeResults;
    }
}
