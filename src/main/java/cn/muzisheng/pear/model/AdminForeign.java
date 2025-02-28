package cn.muzisheng.pear.model;

import lombok.Data;

/**
 * 外部表名、当前表外键字段名称、当前表外键字段私用名称、外部表中字段名称
 **/
@Data
public class AdminForeign {
    /**
     * 当前表外键字段名称
     **/
    private String field;
    /**
     * 当前表外键字段数据库中的名称
     **/
    private String fieldName;
    /**
     * 外键路径
     **/
    private String path;
}
