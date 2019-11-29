package cn.luoyulingfeng.dbindexresearch.index;

import cn.luoyulingfeng.dbindexresearch.util.HashUtil;
import cn.luoyulingfeng.dbindexresearch.util.MathUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.*;

/**
 * 线性哈希算法
 */
@SuppressWarnings({"DuplicatedCode", "Duplicates"})
public class LinearHash {

    private List<Block> blocks;
    private int totalKeys;
    private final float AVERAGE_OCCUPANCY_RATE_THRESHOLD = 0.75f;

    public LinearHash() {
        totalKeys = 0;
        blocks = new ArrayList<>();
        blocks.add(new Block());
        blocks.add(new Block());
    }

    /**
     * 插入元素
     * @param key       索引键
     * @param value     索引值
     */
    public void insert(String key, Object value){

        int n = blocks.size();//桶的数量
        int i = (int)Math.ceil(MathUtil.log2(n));//桶的数量换算成二进制需要的位数
        int m = hash(key, n);
        //获取下标为m的桶
        Block block = blocks.get(m);

        //循环，直到找到一个没有装满的桶
        Block finalBlock = findNotFullBlock(block);
        int emptyIndex = findFirstEmptyIndex(finalBlock);
        finalBlock.values[emptyIndex] = new Data(key, value);
        finalBlock.valuesCounter++;
        totalKeys++;

        //计算平均入住率，如果大于阈值则需要增加新桶
        double averageOccupancy = totalKeys*1f / (n * Block.BLOCK_SIZE);
        if (averageOccupancy > AVERAGE_OCCUPANCY_RATE_THRESHOLD){
            //添加新桶
            Block newBlock = new Block();
            blocks.add(newBlock);

            int realN = n - (int)Math.pow(2, i-1);
            int j = (int)Math.ceil(MathUtil.log2(n + 1));
            Block currentBlock = blocks.get(realN);
            while(true){
                for (int x=0; x<currentBlock.values.length; x++){
                    Data data = currentBlock.values[x];
                    if (data == null){
                        continue;
                    }
                    int dataKeyHash = HashUtil.time33(data.key);
                    int dataM = dataKeyHash % (int)Math.pow(2, j);
                    if (dataM == n){
                        finalBlock = findNotFullBlock(newBlock);
                        emptyIndex = findFirstEmptyIndex(finalBlock);
                        finalBlock.values[emptyIndex] = data.copy();
                        finalBlock.valuesCounter++;
                        currentBlock.values[x] = null;
                        currentBlock.valuesCounter--;
                    }
                }
                if(currentBlock.nextBlock == null){
                    break;
                }
                currentBlock = currentBlock.nextBlock;
            }
        }
    }

    /**
     * 根据第一个桶找到后续的第一个不满的桶
     * @param firstBlock
     * @return
     */
    private Block findNotFullBlock(Block firstBlock){
        Block tmpBlock = firstBlock;
        while (tmpBlock.valuesCounter == Block.BLOCK_SIZE){
            if (tmpBlock.nextBlock == null){
                tmpBlock.nextBlock = new Block();
                tmpBlock = tmpBlock.nextBlock;
                break;
            }
            tmpBlock = tmpBlock.nextBlock;
        }
        return tmpBlock;
    }

    /**
     * 找到某个块儿中第一个数据为null的位置的下标
     * @param block
     * @return
     */
    private int findFirstEmptyIndex(Block block){
        int index = 0;
        for (int i=0; i<block.values.length; i++){
            if (block.values[i] == null){
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * 打印索引结构
     * @return
     */
    public String structure(){
        JSONArray blockArray = new JSONArray();
        for (int i=0; i<blocks.size(); i++){
            JSONArray currentArray = new JSONArray();
            int j = 0;
            Block currentBlock = blocks.get(i);
            while(true){
                JSONObject blockInfo = new JSONObject();
                blockInfo.put("name", String.format("bucket%d-%d", i, j++));
                blockInfo.put("values", currentBlock.valuesCounter);
                currentArray.add(blockInfo);
                if (currentBlock.nextBlock == null){
                    break;
                }
                currentBlock = currentBlock.nextBlock;
            }
            blockArray.add(currentArray);
        }
        return blockArray.toJSONString();
    }

    /**
     * 根据键值查找符合条件的记录
     * @param key 键值
     * @return
     */
    public List<Object> find(String key){
        List<Object> objectList = new ArrayList<>();
        Set<Integer> indexSet = new HashSet<>();
        for (int x=2; x<=blocks.size(); x++){
            int m = hash(key, x);
            if (indexSet.contains(m)){
                continue;
            }
            indexSet.add(m);

            //获取下标为m的桶
            Block block = blocks.get(m);

            //遍历该桶及其溢出桶，收集符合条件的数据
            while (true){
                for (int y=0; y<block.values.length; y++){
                    Data data = block.values[y];
                    if (data == null){
                        continue;
                    }
                    if (data.key.equals(key)){
                        objectList.add(data.value);
                    }
                }
                if (block.nextBlock == null){
                    break;
                }
                block = block.nextBlock;
            }
        }
        return objectList;
    }

    /**
     * 删除某个键值对应数据
     * @param key 键值
     */
    public void delete(String key){
        Set<Integer> indexSet = new HashSet<>();
        for (int x=2; x<=blocks.size(); x++){
            int m = hash(key, x);
            if (indexSet.contains(m)){
                continue;
            }
            indexSet.add(m);

            //获取下标为m的桶
            Block block = blocks.get(m);

            //遍历该桶及其溢出桶，收集符合条件的数据
            while (true){
                for (int y=0; y<block.values.length; y++){
                    Data data = block.values[y];
                    if (data == null){
                        continue;
                    }
                    if (data.key.equals(key)){
                        block.values[y] = null;
                        block.valuesCounter--;
                        totalKeys--;
                    }
                }
                if (block.nextBlock == null){
                    break;
                }
                block = block.nextBlock;
            }
        }
    }

    /**
     * 线性哈希函数
     * @param key 键值
     * @param n 哈希桶数量
     * @return
     */
    private int hash(String key, int n){
        int i = (int)Math.ceil(MathUtil.log2(n));//桶的数量换算成二进制需要的位数
        int k = HashUtil.time33(key);//key的hash值
        k = k >= 0? k: 0-k;
        int m = k % (int)Math.pow(2, i);//逻辑桶下标

        //如果计算真桶的下标
        int realM = m;
        if (m >= n){
            realM = m - (int)Math.pow(2, i - 1);
        }
        return realM;
    }

    /**
     * 数据桶
     */
    public static class Block{
        public static final int BLOCK_SIZE = 4096;
        public Data[] values = new Data[BLOCK_SIZE];
        public int valuesCounter;
        public Block nextBlock;
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
