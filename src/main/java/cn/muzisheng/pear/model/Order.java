package cn.muzisheng.pear.model;

import lombok.Data;

/**
 * 所有字段默认排序方式的列表
 **/
@Data
public class Order {
    /**
     * 字段名
     **/
    private String Name;
    /**
     * 排序方式
     **/
    private String Op;
}
