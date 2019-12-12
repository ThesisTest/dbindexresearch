package cn.luoyulingfeng.dbindexresearch.index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * B+树算法
 */
@SuppressWarnings("Duplicates")
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
            childNodes.add(leftNode);
            childNodes.add(rightNode);

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
            if (key.compareTo(last.key) > 0){
                currentNode = currentNode.nextNode;
                continue;
            }
            int index = 0;
            for (int i=0; i<currentNode.dataList.size(); i++){
                Data data = currentNode.dataList.get(i);
                if (data.key.equals(key)){
                    index = i;
                    break;
                }
            }
            for (int i=index; i<currentNode.dataList.size(); i++){
                Data data = currentNode.dataList.get(i);
                if (data.key.compareTo(key) > 0){
                    break;
                }
                objectList.add(data.value);
            }
            currentNode = currentNode.nextNode;
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

    /**
     * 搜索符合条件的数据
     * @param key 键值
     * @param node 节点
     * @param mode 模式
     * @return
     */
    private Object search(String key, Node node, String mode) {

        //如果是叶子节点则直接取值
        if (node.isLeaf()) {
            List<Data> dataList = node.dataList;
            for (Data data : dataList) {
                if (data.key.equals(key)) {
                    switch (mode) {
                        case NODE:
                            return node;
                        case INT:
                            return data.value;
                    }
                }
            }
            return null;
        }


        List<Node> nodes = node.children;
        //如果寻找的key小于节点的键的最小值
        String minKey = node.dataList.get(0).key;
        if (key.compareTo(minKey) < 0) {
            for (Node n : nodes) {
                List<Data> dataList = n.dataList;
                //找到子节点集合中最大键小于父节点最小键节点
                if (dataList.get(dataList.size() - 1).key.compareTo(minKey) < 0) {
                    return search(key, n, mode);
                }
            }
        }

        //如果寻找的key大于节点的键的最大值
        String maxKey = getMaxKeyInNode(node);
        if (key.compareTo(maxKey) >= 0) {
            for (Node n : nodes) {
                List<Data> dataList = n.dataList;
                //找到子节点集合中最小键大于等于父节点最小大键节点
                if (dataList.get(0).key.compareTo(maxKey) >= 0) {
                    return search(key, n, mode);
                }
            }
        }

        //如果寻找的key在最大值和最小值之间，首先定位到最窄的区间
        String min = getLeftBoundOfKey(node, key);
        String max = getRightBoundOfKey(node, key);


        //去所有的子节点中找键的范围在min和max之间的节点
        for (Node n : nodes) {
            List<Data> dataList = n.dataList;
            //找到子节点集合中键的范围在min和max之间的节点
            if (dataList.get(0).key.compareTo(min) >= 0 && dataList.get(dataList.size() - 1).key.compareTo(max) < 0) {
                return search(key, n, mode);
            }
        }
        return null;
    }

    /**
     * 获取node中最小的键
     * @param node 节点
     * @return
     */
    private String getMinKeyInNode(Node node) {
        List<Data> keyAndValues = node.dataList;
        return keyAndValues.get(0).key;
    }

    /**
     * 获取节点中最大的键
     * @param node 节点
     * @return
     */
    private String getMaxKeyInNode(Node node) {
        List<Data> dataList = node.dataList;
        return dataList.get(dataList.size() - 1).key;
    }

    /**
     * 获取key左边相邻和键值
     * @param node 节点
     * @param key 键值
     * @return
     */
    private String getLeftBoundOfKey(Node node, String key) {
        String left = "";
        List<Data> dataList = node.dataList;
        for (int i = 0; i < dataList.size() - 1; i++) {
            if (dataList.get(i).key.compareTo(key) <= 0 && dataList.get(i + 1).key.compareTo(key) > 0) {
                left = dataList.get(i).key;
                break;
            }
        }
        return left;
    }

    /**
     * 获取key右边相邻的键值
     * @param node 节点
     * @param key 键值
     * @return
     */
    private String getRightBoundOfKey(Node node, String key) {
        String right = "";
        List<Data> dataList = node.dataList;
        for (int i = 0; i < dataList.size() - 1; i++) {
            if (dataList.get(i).key.compareTo(key) <= 0 && dataList.get(i + 1).key.compareTo(key) > 0) {
                right = dataList.get(i + 1).key;
                break;
            }
        }
        return right;
    }

    /**
     * @param key
     * @return
     */
    public boolean delete(String key) {

        //首先找到要删除的key所在的节点
        Node deleteNode = (Node) search(key, root, NODE);
        //如果没找到则删除失败
        if (deleteNode == null) {
            return false;
        }

        if (deleteNode == root) {
            delKeyAndValue(root.dataList, key);
            return true;
        }

        if (deleteNode == head && isNeedMerge(head)) {
            head = head.nextNode;
        }

        return merge(deleteNode, key);
    }

    /**
     * 判断节点是否需要合并
     * @param node 节点
     * @return
     */
    private boolean isNeedMerge(Node node) {
        if (node == null) {
            return false;
        }
        List<Data> dataList = node.dataList;
        return dataList.size() < rank / 2;
    }

    /**
     * 删除数据
     * @param dataList 数据列表
     * @param key 键值
     */
    private void delKeyAndValue(List<Data> dataList, String key) {
        for (Data data : dataList) {
            if (data.key.equals(key)) {
                dataList.remove(data);
                break;
            }
        }
    }

    private boolean merge(Node node, String key) {
        List<Data> dataList = node.dataList;

        //首先删除该key-vaule
        delKeyAndValue(dataList, key);
        //如果要删除的节点的键值对的数目小于节点最大键值对数目*填充因子
        if (isNeedMerge(node)) {
            Boolean isBalance;

            //如果左节点有富余的键值对，则取一个到当前节点
            Node preNode = getPreviousNode(node);
            isBalance = balanceNode(node, preNode, PRE_NODE);

            //如果此时已经平衡，则已经删除成功
            if (isBalance)
                return true;

            //如果右兄弟节点有富余的键值对，则取一个到当前节点
            Node nextNode = getNextNode(node);
            isBalance = balanceNode(node, nextNode, NEXT_NODE);

            return isBalance || mergeNode(node, key);
        } else {
            return true;
        }
    }

    /**
     * 获取node前面的节点
     * @param node 节点
     * @return
     */
    private Node getPreviousNode(Node node) {
        if (node.isRoot()) {
            return null;
        }

        Node parentNode = node.parentNode;

        //得到兄弟节点
        List<Node> nodes = parentNode.children;
        List<Data> dataList = new ArrayList<>();
        for (Node n : nodes) {
            List<Data> list = n.dataList;
            String maxKey = list.get(list.size() - 1).key;
            if (maxKey.compareTo(getMinKeyInNode(node)) < 0) {
                dataList.add(new Data(maxKey, n));
            }
        }
        Collections.sort(dataList);
        if (dataList.isEmpty()) {
            return null;
        }
        return (Node) dataList.get(dataList.size() - 1).value;
    }


    /**
     * 获取node之前的节点
     * @param node 节点
     * @return
     */
    private Node getNextNode(Node node) {
        if (node.isRoot()) {
            return null;
        }

        Node parentNode = node.parentNode;
        //得到兄弟节点
        List<Node> nodes = parentNode.children;
        List<Data> dataList = new ArrayList<>();
        for (Node n : nodes) {
            List<Data> list = n.dataList;
            String minKey = list.get(0).key;
            if (minKey.compareTo(getMaxKeyInNode(node)) > 0) {
                dataList.add(new Data(minKey, n));
            }
        }
        Collections.sort(dataList);
        if (dataList.isEmpty()) {
            return null;
        }
        return (Node) dataList.get(0).value;
    }


    /**
     * 平衡node节点机器兄弟节点
     * @param node 节点
     * @param brotherNode 兄弟节点
     * @param nodeType 节点类型
     * @return
     */
    private boolean balanceNode(Node node, Node brotherNode, String nodeType) {
        if (brotherNode == null) {
            return false;
        }

        List<Data> dataList = node.dataList;
        if (isMoreElement(brotherNode)) {
            List<Data> brotherDataList = brotherNode.dataList;
            int brotherSize = brotherDataList.size();

            //兄弟节点删除挪走的键值对
            Data data = null;
            Data data1 = null;
            switch (nodeType) {
                case PRE_NODE:
                    data = brotherDataList.remove(brotherSize - 1);
                    data1 = getDataInMinAndMax(node.parentNode, data.key, getMinKeyInNode(node));
                    if(data1 != null){
                        data1.key = data.key;
                    }
                    break;
                case NEXT_NODE:
                    data = brotherDataList.remove(0);
                    data1 = getDataInMinAndMax(node.parentNode, getMaxKeyInNode(node), data.key);
                    if (data1 != null){
                        data1.key = brotherDataList.get(0).key;
                    }
                    break;
            }
            //当前节点添加从前一个节点得来的键值对
            dataList.add(data);

            //对键值对重排序
            Collections.sort(dataList);
            return true;
        }
        return false;
    }

    /**
     * 判断一个节点是否有富余的键值对
     * @param node 节点
     * @return
     */
    private boolean isMoreElement(Node node) {
        return node != null && (node.dataList.size() > rank / 2);
    }

    /**
     * 找到node的键值对中在min和max中的键值对
     * @param node 节点
     * @param min 最小键值
     * @param max 最大键值
     * @return
     */
    private Data getDataInMinAndMax(Node node, String min, String max) {
        if (node == null) {
            return null;
        }
        List<Data> dataList = node.dataList;
        Data data = null;
        for (Data d : dataList) {
            if (d.key.compareTo(min) > 0 && d.key.compareTo(max) <= 0) {
                data = d;
                break;
            }
        }
        return data;
    }

    /**
     * 合并节点
     * @param node 节点
     * @param key 键值
     * @return
     */
    private boolean mergeNode(Node node, String key) {
        if (node.isRoot()) {
            return false;
        }
        Node preNode;
        Node nextNode;
        Node parentNode = node.parentNode;
        List<Node> childNodes = parentNode.children;
        List<Node> childNodes1 = node.children;
        List<Data> parentKeyAndValue = parentNode.dataList;
        List<Data> keyAndValues = node.dataList;

        if (node.isLeaf()) {
            if (parentKeyAndValue.size() == 1 && parentNode != root) {
                return true;
            }
            preNode = getPreviousNode(node);
            nextNode = getNextNode(node);
            if (preNode != null) {
                List<Data> preKeyAndValues = preNode.dataList;
                keyAndValues.addAll(preKeyAndValues);
                if (preNode.isHead()) {
                    head = node;
                    node.previousNode = null;
                } else {
                    preNode.previousNode.nextNode = node;
                    node.previousNode = preNode.previousNode;
                }
                //将合并后节点的后节点设置为当前节点的后节点
                preNode.nextNode = node.nextNode;
                Data keyAndValue = getDataInMinAndMax(parentNode, getMinKeyInNode(preNode), key);
                delKeyAndValue(parentKeyAndValue, keyAndValue.key);
                if (parentKeyAndValue.isEmpty()) {
                    root = node;
                } else {
                    //删除当前节点
                    childNodes.remove(preNode);
                }
                Collections.sort(keyAndValues);
                merge(parentNode, key);
                return true;
            }

            if (nextNode != null) {
                List<Data> nextKeyAndValues = nextNode.dataList;
                keyAndValues.addAll(nextKeyAndValues);
                if (nextNode.isTail()) {
                    node.previousNode = null;
                } else {
                    nextNode.nextNode.previousNode = node;
                    node.nextNode = nextNode.nextNode;
                }

                Data keyAndValue = getDataInMinAndMax(parentNode, key, getMinKeyInNode(nextNode));
                delKeyAndValue(parentKeyAndValue, keyAndValue.key);
                if (parentKeyAndValue.isEmpty()) {
                    root = node;
                    node.parentNode = null;
                } else {
                    //删除当前节点
                    childNodes.remove(nextNode);
                }
                Collections.sort(keyAndValues);
                merge(parentNode, key);
                return true;
            }
            //前节点和后节点都等于null那么是root节点
            return false;
        } else {
            preNode = getPreviousNode(node);
            nextNode = getNextNode(node);
            if (preNode != null) {
                //将前一个节点和当前节点还有父节点中的相应Key-value合并
                List<Data> preKeyAndValues = preNode.dataList;
                preKeyAndValues.addAll(keyAndValues);
                String min = getMaxKeyInNode(preNode);
                String max = getMinKeyInNode(node);

                //父节点中移除这个key-value
                Data keyAndValue = getDataInMinAndMax(parentNode, min, max);
                parentKeyAndValue.remove(keyAndValue);
                if (parentKeyAndValue.isEmpty()) {
                    root = preNode;
                    node.parentNode = null;
                    preNode.parentNode = null;
                } else {
                    childNodes.remove(node);
                }
                //assert nextNode != null;
                preNode.nextNode = nextNode != null? nextNode.nextNode: null;

                //前节点加上一个当前节点的所有子节点中最小key的key-value
                Data minKeyAndValue = getMinDataInChildNode(node);
                assert minKeyAndValue != null;
                Data keyAndValue1 = new Data(minKeyAndValue.key, minKeyAndValue.value);
                preKeyAndValues.add(keyAndValue1);
                List<Node> preChildNodes = preNode.children;
                preChildNodes.addAll(node.children);

                //将当前节点的孩子节点的父节点设为当前节点的后节点
                for (Node node1 : childNodes1) {
                    node1.parentNode = preNode;
                }
                Collections.sort(preKeyAndValues);
                merge(parentNode, key);
                return true;
            }

            if (nextNode != null) {
                //将后一个节点和当前节点还有父节点中的相应Key-value合并
                List<Data> nextKeyAndValues = nextNode.dataList;
                nextKeyAndValues.addAll(keyAndValues);

                String min = getMaxKeyInNode(node);
                String max = getMinKeyInNode(nextNode);
                //父节点中移除这个key-value
                Data keyAndValue = getDataInMinAndMax(parentNode, min, max);
                parentKeyAndValue.remove(keyAndValue);
                childNodes.remove(node);
                if (parentKeyAndValue.isEmpty()) {
                    root = nextNode;
                    nextNode.parentNode = null;
                } else {
                    childNodes.remove(node);
                }
                nextNode.previousNode = node.previousNode;

                //后节点加上一个当后节点的所有子节点中最小key的key-value
                Data minKeyAndValue = getMinDataInChildNode(nextNode);
                assert minKeyAndValue != null;
                Data keyAndValue1 = new Data(minKeyAndValue.key, minKeyAndValue.value);
                nextKeyAndValues.add(keyAndValue1);
                List<Node> nextChildNodes = nextNode.children;
                nextChildNodes.addAll(node.children);

                //将当前节点的孩子节点的父节点设为当前节点的后节点
                for (Node node1 : childNodes1) {
                    node1.parentNode = nextNode;
                }
                Collections.sort(nextKeyAndValues);
                merge(parentNode, key);
                return true;
            }
            return false;
        }
    }

    /**
     * 获取节点中的最小数据
     * @param node 节点
     * @return
     */
    private Data getMinDataInChildNode(Node node) {
        if (node.children == null || node.children.isEmpty()) {
            return null;
        }
        List<Data> sortKeyAndValues = new ArrayList<>();
        List<Node> childNodes = node.children;
        for (Node childNode : childNodes) {
            List<Data> keyAndValues = childNode.dataList;
            Data minKeyAndValue = keyAndValues.get(0);
            sortKeyAndValues.add(minKeyAndValue);
        }
        Collections.sort(sortKeyAndValues);
        return sortKeyAndValues.get(0);
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
