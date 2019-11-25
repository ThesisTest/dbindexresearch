package cn.luoyulingfeng.dbindexresearch.util;

public class HashUtil {

    /**
     * times33哈希算法
     * @param value
     * @return
     */
    public static int time33(String value) {
        int hash = 5381;
        for (int i = 0; i < value.length(); i++) {
            hash = ((hash << 5) + hash) + value.charAt(i);
        }
        return hash;
    }
}
