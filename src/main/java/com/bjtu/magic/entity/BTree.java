package com.bjtu.magic.entity;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * @Date: 2020/4/17 19:43
 * @Description: 索引结构
 */
public class BTree <T, K extends Comparable<K>> implements Serializable {
    private static final long serialVersionUID=1L;
    //B+树的阶
    private Integer degree;
    //最大节点数
    private Integer maxAmount;
    //根节点
    private Node<T, K> root;
    //最右叶子节点
    private LeafNode<T, K> left;
    //默认3阶
    public BTree(){
        this(3);
    }
    public BTree(Integer degree){
        this.degree = degree;
        this.maxAmount = degree+1;
        this.root = new LeafNode();
        this.left = null;
    }
    public void printRoot(){
        System.out.println("root is :"+ JSON.toJSONString(root.keys));
    }
    public void printLeafAmount(){
        int leafAmount = 0;
        while(left!=null){
            leafAmount++;
            left=left.right;
        }
        System.out.println("leaf's amount is :"+leafAmount);
    }
    //查询
    public T find(K key){
        T t = this.root.find(key);
        return t;
    }
    //插入
    public void insert(T value, K key){
        if(key == null) {
            return;
        }
        Node<T, K> t = this.root.insert(value, key);
        if(t != null) {
            this.root = t;
        }
        //B+数的最左叶子节点
        this.left = (LeafNode<T, K>)this.root.getNewLeft();
    }

    public int delete(K key) {
        return this.root.delete(key);
    }
    public void update(T t,K k){
        this.root.update(t,k);
    }

    /**
     * 节点父类，因为在B+树中，非叶子节点不用存储具体的数据，只需要把索引作为键就可以了
     * 所以叶子节点和非叶子节点的类不太一样，但是又会公用一些方法，所以用Node类作为父类,
     * 而且因为要互相调用一些公有方法，所以使用抽象类
     */
    abstract class Node<T, K extends Comparable<K>> implements Serializable{
        //父节点
        protected Node<T, K> parent;
        //子节点
        protected Node<T, K>[] childs;
        //子节点数量
        protected Integer amount;
        //关键字数组
        protected Object keys[];

        //构造方法
        public Node(){
            this.keys = new Object[maxAmount];
            this.childs = new Node[maxAmount];
            this.amount = 0;
            this.parent = null;
        }

        abstract T find(K key);
        abstract Node insert(T value, K key);
        abstract LeafNode getNewLeft();
        abstract int delete(K key);
        abstract void update(T value,K key);
    }


    /**
     * 非叶节点类 只存放关键字
     */
    private class KeyNode<T, K extends Comparable<K>> extends Node<T,K> implements Serializable{

        @Override
        void update(T value, K key) {
            int i = 0;
            while(i < this.amount){
                if(key.compareTo((K) this.keys[i]) <= 0) {
                    break;
                }
                i++;
            }
            if(this.amount == i){
                return;
            }
            this.childs[i].update(value,key);
        }

        @Override
        int delete(K key) {
            int i = 0;
            while(i < this.amount){
                if(key.compareTo((K) this.keys[i]) <= 0) {
                    break;
                }
                i++;
            }
            if(this.amount == i) {
                return 0;
            }
            return this.childs[i].delete(key);
        }

        /**
         * 递归查找,这里只是为了确定值究竟在哪一块,真正的查找到叶子节点才会查
         * @param key
         * @return
         */
        @Override
        T find(K key) {
            int i = 0;
            while(i < this.amount){
                if(key.compareTo((K) this.keys[i]) <= 0) {
                    break;
                }
                i++;
            }
            if(this.amount == i) {
                return null;
            }
            return this.childs[i].find(key);
        }

        /**
         * 递归插入,先把值插入到对应的叶子节点,最终讲调用叶子节点的插入类
         * @param value
         * @param key
         */
        @Override
        Node<T, K> insert(T value, K key) {
            int i = 0;
            while(i < this.amount){
                if(key.compareTo((K) this.keys[i]) < 0)
                    break;
                i++;
            }
            if(key.compareTo((K) this.keys[this.amount - 1]) >= 0) {
                i--;
            }
            return this.childs[i].insert(value, key);
        }

        @Override
        LeafNode<T, K> getNewLeft() {
            return this.childs[0].getNewLeft();
        }

        /**
         * 当叶子节点插入成功完成分解时,递归地向父节点插入新的节点以保持平衡
         */
        protected Node insertNode(Node node1, Node node2, K key){
            K oldKey = null;
            if(this.amount > 0) {
                oldKey = (K) this.keys[this.amount - 1];
            }
            //如果原有key为null,说明这个非节点是空的,直接放入两个节点即可
            if(key == null || this.amount <= 0){
                this.keys[0] = node1.keys[node1.amount - 1];
                this.keys[1] = node2.keys[node2.amount - 1];
                this.childs[0] = node1;
                this.childs[1] = node2;
                this.amount += 2;
                return this;
            }
            //原有节点不为空,则应该先寻找原有节点的位置,然后将新的节点插入到原有节点中
            int i = 0;
            while(key.compareTo((K)this.keys[i]) != 0){
                i++;
            }
            //左边节点的最大值可以直接插入,右边的要挪一挪再进行插入
            this.keys[i] = node1.keys[node1.amount - 1];
            this.childs[i] = node1;

            Object tempKeys[] = new Object[maxAmount];
            Object tempChilds[] = new Node[maxAmount];

            System.arraycopy(this.keys, 0, tempKeys, 0, i + 1);
            System.arraycopy(this.childs, 0, tempChilds, 0, i + 1);
            System.arraycopy(this.keys, i + 1, tempKeys, i + 2, this.amount - i - 1);
            System.arraycopy(this.childs, i + 1, tempChilds, i + 2, this.amount - i - 1);
            tempKeys[i + 1] = node2.keys[node2.amount - 1];
            tempChilds[i + 1] = node2;

            this.amount++;

            //判断是否需要拆分
            //如果不需要拆分,把数组复制回去,直接返回
            if(this.amount <= degree){
                System.arraycopy(tempKeys, 0, this.keys, 0, this.amount);
                System.arraycopy(tempChilds, 0, this.childs, 0, this.amount);
                return null;
            }
            //如果需要拆分,和拆叶子节点时类似,从中间拆开
            Integer middle = this.amount / 2;
            //新建非叶子节点,作为拆分的右半部分
            KeyNode tempNode = new KeyNode();
            //非叶节点拆分后应该将其子节点的父节点指针更新为正确的指针
            tempNode.amount = this.amount - middle;
            tempNode.parent = this.parent;
            //如果父节点为空,则新建一个非叶子节点作为父节点,并且让拆分成功的两个非叶子节点的指针指向父节点
            if(this.parent == null) {
                KeyNode tempKeyNode = new KeyNode();
                tempNode.parent = tempKeyNode;
                this.parent = tempKeyNode;
                oldKey = null;
            }
            System.arraycopy(tempKeys, middle, tempNode.keys, 0, tempNode.amount);
            System.arraycopy(tempChilds, middle, tempNode.childs, 0, tempNode.amount);
            for(int j = 0; j < tempNode.amount; j++){
                tempNode.childs[j].parent = tempNode;
            }
            //让原有非叶子节点作为左边节点
            this.amount = middle;
            this.keys = new Object[maxAmount];
            this.childs = new Node[maxAmount];
            System.arraycopy(tempKeys, 0, this.keys, 0, middle);
            System.arraycopy(tempChilds, 0, this.childs, 0, middle);
            //叶子节点拆分成功后,需要把新生成的节点插入父节点
            KeyNode parentNode = (KeyNode)this.parent;
            return parentNode.insertNode(this, tempNode, oldKey);
        }

    }

    /**
     * 叶节点类
     * @param <T>
     * @param <K>
     */
    class LeafNode<T, K extends Comparable<K>> extends Node<T,K> implements Serializable{

        protected Object values[];
        protected LeafNode left;
        protected LeafNode right;

        public LeafNode(){
            super();
            this.values = new Object[maxAmount];
            this.left = null;
            this.right = null;
        }

        /**
         * 二分查找
         */
        @Override
        protected T find(K key) {
            if(this.amount <=0) {
                return null;
            }
            Integer left = 0;
            Integer right = this.amount;
            Integer middle = (left + right) / 2;
            while(left < right){
                K middleKey = (K) this.keys[middle];
                if(key.compareTo(middleKey) == 0) {
                    return (T) this.values[middle];
                }else if(key.compareTo(middleKey) < 0) {
                    right = middle;
                }else {
                    left = middle;
                }
                middle = (left + right) / 2;
            }
            return null;
        }

        @Override
        void update(T t, K key) {
            if(this.amount <=0) {
                return;
            }
            Integer left = 0;
            Integer right = this.amount;
            Integer middle = (left + right) / 2;
            while(left < right){
                K middleKey = (K) this.keys[middle];
                if(key.compareTo(middleKey) == 0) {
                    this.values[middle]=t;
                    return;
                }else if(key.compareTo(middleKey) < 0) {
                    right = middle;
                }else {
                    left = middle;
                }
                middle = (left + right) / 2;
            }
        }

        @Override
        protected int delete(K key) {
            if(this.amount <=0) {
                return 0;
            }
            Integer left = 0;
            Integer right = this.amount;
            Integer middle = (left + right) / 2;
            while(left < right){
                K middleKey = (K) this.keys[middle];
                if(key.compareTo(middleKey) == 0) {
                    this.values[middle]=null;
//                    return (T) this.values[middle];
                    return 1;
                }else if(key.compareTo(middleKey) < 0) {
                    right = middle;
                }else {
                    left = middle;
                }
                middle = (left + right) / 2;
            }
            return 0;
        }

        /**
         * 插入到叶子节点中
         */
        @Override
        protected Node insert(T value, K key) {
            //保存原始存在父节点的key值
            K oldKey = null;
            if(this.amount > 0) {
                oldKey = (K) this.keys[this.amount - 1];
            }
            //先插入数据
            int i = 0;
            while(i < this.amount){
                if(key.compareTo((K) this.keys[i]) < 0)
                    break;
                    i++;
            }

            //复制数组,完成添加
            Object tempKeys[] = new Object[maxAmount];
            Object tempKalues[] = new Object[maxAmount];
            System.arraycopy(this.keys, 0, tempKeys, 0, i);
            System.arraycopy(this.values, 0, tempKalues, 0, i);
            System.arraycopy(this.keys, i, tempKeys, i + 1, this.amount - i);
            System.arraycopy(this.values, i, tempKalues, i + 1, this.amount - i);
            tempKeys[i] = key;
            tempKalues[i] = value;
            this.amount++;
            //判断是否需要拆分
            //如果不需要拆分完成复制后直接返回
            if(this.amount <= degree){
                System.arraycopy(tempKeys, 0, this.keys, 0, this.amount);
                System.arraycopy(tempKalues, 0, this.values, 0, this.amount);

                //有可能虽然没有节点分裂，但是实际上插入的值大于了原来的最大值，所以所有父节点的边界值都要进行更新
                Node node = this;
                while (node.parent != null){
                    K tempkey = (K)node.keys[node.amount - 1];
                    if(tempkey.compareTo((K)node.parent.keys[node.parent.amount - 1]) > 0){
                        node.parent.keys[node.parent.amount - 1] = tempkey;
                        node = node.parent;
                    }
                    else {
                    	break;
                    }
                }
                return null;
            }

            //如果需要拆分,则从中间把节点拆分差不多的两部分
            Integer middle = this.amount / 2;

            //新建叶子节点,作为拆分的右半部分
            LeafNode<T, K> tempNode = new LeafNode<T, K>();
            tempNode.amount = this.amount - middle;
            tempNode.parent = this.parent;
            //如果父节点为空,则新建一个非叶子节点作为父节点,并且让拆分成功的两个叶子节点的指针指向父节点
            if(this.parent == null) {
                KeyNode tempKeyNode = new KeyNode();
                tempNode.parent = (Node)tempKeyNode;
                this.parent = (Node)tempKeyNode;
                oldKey = null;
            }
            System.arraycopy(tempKeys, middle, tempNode.keys, 0, tempNode.amount);
            System.arraycopy(tempKalues, middle, tempNode.values, 0, tempNode.amount);

            //让原有叶子节点作为拆分的左半部分
            this.amount = middle;
            this.keys = new Object[maxAmount];
            this.values = new Object[maxAmount];
            System.arraycopy(tempKeys, 0, this.keys, 0, middle);
            System.arraycopy(tempKalues, 0, this.values, 0, middle);

            this.right = tempNode;
            tempNode.left = this;

            //叶子节点拆分成功后,需要把新生成的节点插入父节点
            KeyNode parentNode = (KeyNode)this.parent;
            return parentNode.insertNode(this, tempNode, oldKey);
        }

        @Override
        protected LeafNode getNewLeft() {
            if(this.amount <= 0) {
                return null;
            }
            return this;
        }
    }
}

