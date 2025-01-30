package cn.muzisheng.pear.entity;

import java.time.LocalDateTime;

/**
 * 货品
 **/
public class Product {
    /**
     * 货品id
     **/
    private String id;
    /**
     * 货品名称
     **/
    private String name;
    /**
     * 用户组id
     **/
    private Long groupId;
    /**
     * 是否启用
     **/
    private Boolean enabled;
    /**
     * 货品模型
     **/
    private productModel model;
    /**
     * 创建时间
     **/
    private LocalDateTime gmtCreated;
    /**
     * 修改时间
     **/
    private LocalDateTime gmtModified;
}
