package com.bjtu.magic.entity;

import java.io.Serializable;

/**
 * @Auther: wjx
 * @Date: 2020/4/19 19:47
 * @Description:表结构文件组成
 *
 */
public class FrmEntity implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 名称
     */
    private String name;
    /**
     * 类型
     */
    private String type;
    /**
     * 长度
     */
    private String length;

    /**
     * 是否为空,默认可空
     */
    private boolean isNull=true;
    /**
     * 是否为主键
     */
    private boolean isKey;

    /**
     * 注释
     */
    private String comment;
    /**
     * 属性顺序
     */
    private int order;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public boolean isNull() {
        return isNull;
    }

    public void setNull(boolean aNull) {
        isNull = aNull;
    }

    public boolean isKey() {
        return isKey;
    }

    public void setKey(boolean key) {
        isKey = key;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FrmEntity{");
        sb.append("name='").append(name).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append(", length='").append(length).append('\'');
        sb.append(", isNull=").append(isNull);
        sb.append(", isKey=").append(isKey);
        sb.append(", comment='").append(comment).append('\'');
        sb.append(", order=").append(order);
        sb.append('}');
        return sb.toString();
    }
}
