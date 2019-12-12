package cn.luoyulingfeng.dbindexresearch.index;

import cn.luoyulingfeng.dbindexresearch.util.HashUtil;
import cn.luoyulingfeng.dbindexresearch.util.MathUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.*;

/**
 * 扩展哈希算法
 */
@SuppressWarnings("Duplicates")
public class ExtendibleHash {

    /**
     * 目录数组
     */
    private Directory directory;
    private List<Bucket> buckets;
    private Map<Integer, Integer> splitBuckets;

    public ExtendibleHash() {
        directory = new Directory();
        buckets = new ArrayList<>();
        splitBuckets = new HashMap<>();
    }

    /**
     * 插入数据
     * @param key 键值
     * @param value 数据
     */
    public void insert(String key, Object value){

        //计算目录索引
        int index = hash(key, directory.depth);

        //如果目录索引对应的数据桶不存在，则创建新桶并插入数据
        if (directory.bucketIndecies[index] == 0){
            Bucket bucket = new Bucket();
            int emptyIndex = findEmptyIndex(bucket);
            bucket.values[emptyIndex] = new Data(key, value);
            bucket.valuesCounter++;
            buckets.add(bucket);
            directory.bucketIndecies[index] = buckets.size();
        }else{
            //获取对应的数据桶
            int bucketIndex = directory.bucketIndecies[index] - 1;
            Bucket bucket = buckets.get(bucketIndex);

            //如果桶没有装满，则插入新数据
            if (bucket.valuesCounter < Bucket.BUCKET_SIZE){
                int emptyIndex = findEmptyIndex(bucket);
                bucket.values[emptyIndex] = new Data(key, value);
                bucket.valuesCounter++;
            }else{
                //如果装满了，则分裂新桶
                Bucket newBucket = new Bucket();
                buckets.add(newBucket);

                //更新桶深度
                bucket.depth++;
                newBucket.depth = bucket.depth;

                //如果桶深大于目录深度则翻倍目录并更新目录深度
                if (bucket.depth > directory.depth){
                    int[] doubledDirectory = new int[2 * directory.bucketIndecies.length];
                    for (int i=0; i<doubledDirectory.length; i++){
                        doubledDirectory[i] = directory.bucketIndecies[i % directory.bucketIndecies.length];
                    }
                    directory.bucketIndecies = doubledDirectory;
                    directory.depth++;
                }

                //将旧桶内的数据重新哈希到新桶中
                int diffIndex = 0;
                for (int i=0; i<bucket.values.length; i++){
                    Data data = bucket.values[i];
                    if (data == null){
                        continue;
                    }
                    int newIndex = hash(data.key, directory.depth);
                    if (newIndex == index){
                        continue;
                    }
                    diffIndex = newIndex;
                    int emptyIndex = findEmptyIndex(newBucket);
                    newBucket.values[emptyIndex] = data.copy();
                    newBucket.valuesCounter++;
                    bucket.values[i] = null;
                    bucket.valuesCounter--;
                }
                //标记新桶
                directory.bucketIndecies[diffIndex] = buckets.size();
                splitBuckets.put(bucketIndex, buckets.size() - 1);

                //将新数据插入对应的桶内
                int newIndex = hash(key, directory.depth);
                Bucket targetBucket = null;
                if (newIndex == index){
                    targetBucket = buckets.get(directory.bucketIndecies[index] - 1);
                }else{
                    targetBucket = buckets.get(directory.bucketIndecies[diffIndex] - 1);
                }
                int emptyIndex = findEmptyIndex(targetBucket);
                targetBucket.values[emptyIndex] = new Data(key, value);
                targetBucket.valuesCounter++;
            }
        }
    }

    /**
     * 查找符合条件的数据
     * @param key 键值
     * @return
     */
    public List<Object> find(String key){

        List<Object> objectList = new ArrayList<>();
        int index = hash(key, directory.depth);
        int bucketIndex = directory.bucketIndecies[index] - 1;
        List<Integer> possibleBuckets = new ArrayList<>();
        possibleBuckets.add(bucketIndex);
        Integer currentKey = bucketIndex;
        while (splitBuckets.containsKey(currentKey)){
            possibleBuckets.add(splitBuckets.get(currentKey));
            currentKey = splitBuckets.get(currentKey);
        }

        for (int possibleBucket: possibleBuckets){
            if (possibleBucket == -1){
                continue;
            }
            Bucket bucket = buckets.get(possibleBucket);
            for (int i=0; i<bucket.values.length; i++){
                Data data = bucket.values[i];
                if (data == null){
                    continue;
                }
                if (data.key.equals(key)){
                    objectList.add(data.value);
                }
            }
        }

        return objectList;
    }

    /**
     * 删除指定键值的数据
     * @param key 键值
     */
    public void delete(String key){
        int index = hash(key, directory.depth);
        int bucketIndex = directory.bucketIndecies[index] - 1;
        List<Integer> possibleBuckets = new ArrayList<>();
        possibleBuckets.add(bucketIndex);
        Integer currentKey = bucketIndex;
        while (splitBuckets.containsKey(currentKey)){
            possibleBuckets.add(splitBuckets.get(currentKey));
            currentKey = splitBuckets.get(currentKey);
        }

        for (int possibleBucket: possibleBuckets){
            Bucket bucket = buckets.get(possibleBucket);
            for (int i=0; i<bucket.values.length; i++){
                Data data = bucket.values[i];
                if (data == null){
                    continue;
                }
                if (data.key.equals(key)){
                    bucket.values[i] = null;
                    bucket.valuesCounter--;
                }
            }
        }

    }


    public int hash(String key, int n){
        int val = HashUtil.time33(key);
        val = val > 0? val: 0-val;
        return val % (int)Math.pow(2, n);
    }

    /**
     * 从数据桶中找到第一个为数据为null的位置
     * @param bucket 数据桶
     * @return
     */
    private int findEmptyIndex(Bucket bucket){
        int index = 0;
        for (int i=0; i<bucket.values.length; i++){
            if (bucket.values[i] == null){
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * 获取结构信息
     * @return
     */
    public String structure(){
        JSONObject struct = new JSONObject();
        struct.put("directory", directory);
        JSONArray bucketsInfo = new JSONArray();
        for (int i=0; i<buckets.size(); i++){
            JSONObject bucketInfo = new JSONObject();
            bucketInfo.put("index", i);
            bucketInfo.put("depth", buckets.get(i).depth);
            bucketInfo.put("size", buckets.get(i).valuesCounter);
            bucketInfo.put("values", buckets.get(i).values);
            bucketsInfo.add(bucketInfo);
        }
        struct.put("buckets", bucketsInfo);
        return struct.toJSONString();
    }


    /**
     * 目录
     */
    public static class Directory{
        public int depth = 1;
        public int[] bucketIndecies = new int[2];
    }


    /**
     * 数据桶
     */
    public static class Bucket{
        public static final int BUCKET_SIZE = 4096;
        public int depth = 1;
        public Data[] values = new Data[BUCKET_SIZE];
        public int valuesCounter;
    }

    /**
     * 数据
     */
    public static class Data{
        public String key;
        public Object value;
        public Data(String key, Object value){
            this.key = key;
            this.value = value;
        }

        public Data copy(){
            return new Data(key, value);
        }
    }

}
