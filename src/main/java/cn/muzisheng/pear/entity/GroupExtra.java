package cn.muzisheng.pear.entity;

import lombok.Data;

/**
 * 用户组扩展信息
 **/
@Data
public class GroupExtra {
    /**
     * 主键
     **/
    private Long id;
    /**
     * 组对象类型
     **/
    private String objectType;
    /**
     * groupId
     **/
    private String objectId;
    /**
     * 键
     **/
    private String key;
    /**
     * 值
     **/
    private String value;
}
