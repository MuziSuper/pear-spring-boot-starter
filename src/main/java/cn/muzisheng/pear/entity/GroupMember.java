package cn.muzisheng.pear.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 组成员
 **/
@Data
public class GroupMember {
    /**
     * 组成员id
     **/
    private Long id;
    /**
     * 用户id
     **/
    private Long userId;
    /**
     * 用户
     **/
    private User user;
    /**
     * 组id
     **/
    private Long groupId;
    /**
     * 组
     **/
    private Group group;
    /**
     * 角色
     **/
    private String role;
    /**
     * 描述
     **/
    private String description;
    /**
     * 创建时间
     **/
    private LocalDateTime gmtCreated;
    /**
     * 修改时间
     **/
    private LocalDateTime gmtModified;

}
