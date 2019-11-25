package cn.luoyulingfeng.dbindexresearch.index;

import cn.luoyulingfeng.dbindexresearch.util.HashUtil;
import cn.luoyulingfeng.dbindexresearch.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 线性哈希算法
 */
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

    public List<Block> getBlocks() {
        return blocks;
    }

    public int getTotalKeys() {
        return totalKeys;
    }

    /**
     * 插入元素
     * @param key       索引键
     * @param value     索引值
     */
    public void insert(String key, Object value){

        int n = blocks.size();//桶的数量
        int i = (int)Math.ceil(MathUtil.log2(n));//桶的数量换算成二进制需要的位数
        int k = HashUtil.time33(key);//key的hash值
        k = k >= 0? k: 0-k;
        int m = k % (int)Math.pow(2, i);//逻辑桶下标

        //如果计算真桶的下标
        int realM = m;
        if (m >= n){
            realM = m - (int)Math.pow(2, i - 1);
        }
        //获取下标为m的桶
        Block block = blocks.get(realM);

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

            //遍历指定的旧桶，将之前应该分配到该新桶的数据移过来
            int realN = n - (int)Math.pow(2, i-1);
            int j = (int)Math.ceil(MathUtil.log2(n + 1));
            Block currentBlock = blocks.get(realN);
            while (true){
                for (int x=0; x<currentBlock.valuesCounter; x++){
                    Data data = currentBlock.values[x];
                    int dataKeyHash = HashUtil.time33(data.key);
                    int dataM = dataKeyHash % (int)Math.pow(2, j);
                    if (dataM == n){
                        //找到一个不满的桶移入数据
                        finalBlock = findNotFullBlock(newBlock);
                        emptyIndex = findFirstEmptyIndex(finalBlock);
                        finalBlock.values[emptyIndex] = data.copy();
                        finalBlock.valuesCounter++;
                        currentBlock.values[x] = null;
                        currentBlock.valuesCounter--;
                    }
                }
                if (currentBlock.nextBlock == null){
                    break;
                }
                currentBlock = currentBlock.nextBlock;
            }
            blocks.add(newBlock);
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
    public int findFirstEmptyIndex(Block block){
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
     * 数据桶
     */
    public static class Block{
        public static final int BLOCK_SIZE = 1024;
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
