package cn.muzisheng.pear.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 所有字段默认排序方式的列表
 **/
@Data
@AllArgsConstructor
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
