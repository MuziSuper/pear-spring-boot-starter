package cn.muzisheng.pear.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户组
 **/
@Data
public class Group {
    /**
     * 用户组id
     **/
    private String id;
    /**
     * 用户组名称
     **/
    private String name;
    /**
     * 用户组类型
     **/
    private String type;
    /**
     * 用户组附加信息
     **/
    private GroupExtra[] extra;
    /**
     * 用户组权限
     **/
    private GroupPermission permission;
    /**
     * 创建时间
     **/
    private LocalDateTime gmtCreated;
    /**
     * 修改时间
     **/
    private LocalDateTime gmtModified;
}
