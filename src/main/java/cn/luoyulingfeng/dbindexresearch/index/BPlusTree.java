package cn.luoyulingfeng.dbindexresearch.index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * B+树算法
 */
public class BPlusTree {

    private static final String NODE = "NODE";
    private static final String INT = "INT";
    private static final String PRE_NODE = "PRENODE";
    private static final String NEXT_NODE = "NEXTNODE";

    //B+树的阶数
    private int rank;
    //根节点
    private Node root;
    //头结点
    private Node head;

    public BPlusTree(int rank){
        this.rank = rank;
    }

    public Node getRoot() {
        return root;
    }

    /**
     * 插入数据
     * @param key 键值
     * @param value 数据
     */
    public void insert(String key, Object value) {
        //插入第一个节点
        if (head == null) {
            List<Data> dataList = new ArrayList<>();
            dataList.add(new Data(key, value));
            head = new Node();
            head.dataList = dataList;
            root = new Node();
            root.dataList = dataList;
        } else {
            Node node = head;
            //遍历链表，找到插入键值对对应的节点
            while (node != null) {
                //如果当前节点是最后一个节点或者要插入的键值对的键的值小于下一个节点的键的最小值，则直接插入当前节点
                if (node.nextNode == null || node.nextNode.dataList.get(0).key.compareTo(key) >= 0) {
                    splitNode(node, key, value);
                    break;
                }

                //移动指针
                node = node.nextNode;
            }
        }
    }

    //判断是否需要拆分节点
    private void splitNode(Node node, String key, Object value) {
        List<Data> dataList = node.dataList;

        if (dataList.size() == rank - 1) {
            //先插入待添加的节点
            dataList.add(new Data(key, value));
            Collections.sort(dataList);

            //取出当前节点的键值对集合
            //取出原来的key-value集合中间位置的下标
            int mid = dataList.size() / 2;

            //取出原来的key-value集合中间位置的键
            String midKey = dataList.get(mid).key;

            //构造一个新的键值对，不是叶子节点的节点不存储value的信息
            Data midData = new Data(midKey, null);

            //将中间位置左边的键值对封装成集合对象
            List<Data> leftDataList = new ArrayList<>();

            for (int i = 0; i < mid; i++) {
                leftDataList.add(dataList.get(i));
            }
            //将中间位置右边边的键值对封装成集合对象
            List<Data> rightDataList = new ArrayList<>();

            //如果是叶子节点则在原节点中保留上移的key-value，否则原节点删除上移的key-value
            int k;
            if (node.isLeaf()) {
                k = mid;
            } else {
                k = mid + 1;
            }
            for (int i = k; i < rank; i++) {
                rightDataList.add(dataList.get(i));
            }

            //以mid为界限将当前节点分列成两个节点，维护前指针和后指针
            Node rightNode;
            Node leftNode;

            //如果是叶子节点维护前后指针
            rightNode = new Node();
            rightNode.dataList = rightDataList;
            rightNode.nextNode = node.nextNode;
            rightNode.parentNode = node.parentNode;

            leftNode = new Node();
            leftNode.dataList = leftDataList;
            leftNode.nextNode = rightNode;
            leftNode.previousNode = node.previousNode;
            leftNode.parentNode = node.parentNode;
            rightNode.previousNode = leftNode;

            //如果当前分裂的节点有孩子节点,设置分裂后节点和孩子节点的关系
            if (node.children != null) {

                //取得所有的孩子节点
                List<Node> nodes = node.children;
                List<Node> leftNodes = new ArrayList<>();
                List<Node> rightNodes = new ArrayList<>();
                for (Node childNode : nodes) {
                    //取得当前孩子节点的最大键值
                    String max = childNode.dataList.get(childNode.dataList.size() - 1).key;
                    if (max.compareTo(midData.key) < 0) {
                        //小于mid处的键的数是左节点的子节点
                        leftNodes.add(childNode);
                        childNode.parentNode = leftNode;
                    } else {
                        //大于mid处的键的数是右节点的子节点
                        rightNodes.add(childNode);
                        childNode.parentNode = rightNode;
                    }
                }
                leftNode.children = leftNodes;
                rightNode.children = rightNodes;
            }

            //分裂节点后将分裂节点的前节点的后节点设置为左节点
            Node preNode = node.previousNode;
            if (preNode != null) {
                preNode.nextNode = leftNode;
            }

            //分裂节点后将分裂节点的后节点的前节点设置为右节点
            Node nextNode = node.nextNode;
            if (nextNode != null) {
                nextNode.previousNode = rightNode;
            }

            //如果由头结点分裂，则分裂后左边的节点为头节点
            if (node == head) {
                head = leftNode;
            }

            //父节点的子节点
            List<Node> childNodes = new ArrayList<>();
            childNodes.add(rightNode);
            childNodes.add(leftNode);

            //分裂当前节点无父节点
            if (node.parentNode == null) {
                //父节点的键值对
                List<Data> parentDataList = new ArrayList<>();
                parentDataList.add(midData);
                //构造父节点
                Node parentNode = new Node();
                parentNode.children = childNodes;
                parentNode.dataList = parentDataList;

                //将子节点与父节点关联
                rightNode.parentNode = parentNode;
                leftNode.parentNode = parentNode;

                //当前节点为根节点
                root = parentNode;
            } else {
                Node parentNode = node.parentNode;
                //将原来的孩子节点（除了被拆分的节点）和新的孩子节点（左孩子和右孩子）合并之后与父节点关联

                childNodes.addAll(parentNode.children);
                //移除正在被拆分的节点
                childNodes.remove(node);

                //将子节点与父节点关联
                parentNode.children = childNodes;
                rightNode.parentNode = parentNode;
                leftNode.parentNode = parentNode;
                if (parentNode.parentNode == null) {
                    root = parentNode;
                }
                //当前节点有父节点,递归调用拆分的方法,将父节点拆分
                splitNode(parentNode, midData.key, midData.value);
            }
        } else {
            dataList.add(new Data(key, value));
            Collections.sort(dataList);
        }
    }

    /**
     * 等值搜索
     * @param key 键值
     * @return
     */
    public List<Object> find(String key){
        List<Object> objectList = new ArrayList<>();
        Node currentNode = head;
        while (currentNode != null){
            Data first = currentNode.dataList.get(0);
            Data last = currentNode.dataList.get(currentNode.dataList.size() - 1);
            if (first.key.compareTo(key) <= 0 && last.key.compareTo(key) >= 0){
                for (Data data: currentNode.dataList){
                    if (data.key.equals(key)){
                        objectList.add(data.value);
                    }
                }
                break;
            }else{
                currentNode = currentNode.nextNode;
            }
        }
        return objectList;
    }


    /**
     * 范围搜索
     * @param from 最小值
     * @param to 最大值
     * @return
     */
    public List<Object> find(String from, String to){
        List<Object> objectList = new ArrayList<>();
        if (from.compareTo(to) > 0){
            return objectList;
        }

        Node currentNode = head;
        while (currentNode != null){
            Data first = currentNode.dataList.get(0);
            Data last = currentNode.dataList.get(currentNode.dataList.size() - 1);
            boolean fromExist = first.key.compareTo(from) >= 0 || last.key.compareTo(from) >= 0;
            boolean toExist = first.key.compareTo(to) <= 0 && last.key.compareTo(to) <= 0;
            if (fromExist || toExist){
                for (Data data: currentNode.dataList){
                    if (data.key.compareTo(from) >= 0 && data.key.compareTo(to) <= 0){
                        objectList.add(data.value);
                    }
                }
            }
            currentNode = currentNode.nextNode;
        }

        return objectList;
    }


    /**
     * 打印B+树
     * @param root
     */
    public void printBtree(Node root) {
        if (root == this.root) {
            //打印根节点内的元素
            printNode(root);
            System.out.println();
        }
        if (root == null) {
            return;
        }

        //打印子节点的元素
        if (root.children != null) {
            //找到最左边的节点
            Node leftNode = null;
            Node tmpNode = null;
            List<Node> childNodes = root.children;
            for (Node node : childNodes) {
                if (node.previousNode == null) {
                    leftNode = node;
                    tmpNode = node;
                }
            }

            while (leftNode != null) {
                //从最左边的节点向右打印
                printNode(leftNode);
                System.out.print("|");
                leftNode = leftNode.nextNode;
            }
            System.out.println();
            printBtree(tmpNode);
        }
    }

    //打印一个节点内的元素
    private void printNode(Node node) {
        List<Data> dataList = node.dataList;
        for (int i = 0; i < dataList.size(); i++) {
            if (i != (dataList.size() - 1)) {
                System.out.print(dataList.get(i).key + ",");
            } else {
                System.out.print(dataList.get(i).key);
            }
        }
    }

    /*节点类*/
    public static class Node {

        //节点的子节点
        public List<Node> children;
        //节点的键值对
        public List<Data> dataList;
        //节点的后节点
        public Node nextNode;
        //节点的前节点
        public Node previousNode;
        //节点的父节点
        public Node parentNode;

        boolean isLeaf() {
            return children==null;
        }

        boolean isHead() {
            return previousNode == null;
        }

        boolean isTail() {
            return nextNode == null;
        }

        boolean isRoot() {
            return parentNode == null;
        }
    }

    /**
     * 数据类
     */
    public static class Data implements Comparable<Data>{
        public String key;
        public Object value;

        public Data(String key, Object value){
            this.key = key;
            this.value = value;
        }

        @Override
        public int compareTo(Data o) {
            if(key.compareTo(o.key) < 0){
                return -1;
            }else if(key.compareTo(o.key) == 0){
                return 0;
            }else{
                return 1;
            }
        }
    }
}
