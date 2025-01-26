package cn.muzisheng.pear.model;

import lombok.Data;

/**
 * 外部表名、当前表外键字段名称、当前表外键字段私用名称、外部表中字段名称
 **/
@Data
public class AdminForeign {
    /**
     * 外部表名
     **/
    private String path;
    /**
     * 当前表外键字段名称
     **/
    private String field;
    /**
     * 当前表外键字段私用名称
     **/
    private String fieldName;
    /**
     * 外部表中字段名称
     **/
    private String foreignKey;
}
