package cn.muzisheng.pear.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 组成员
 **/
@Data
@Entity
@TableName("group_member")
public class GroupMember {
    /**
     * 组成员id
     **/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 用户id
     **/
    private Long userId;
    /**
     * 用户
     **/

    @Column(columnDefinition = "LONGBLOB")
    private String user;
    /**
     * 组id
     **/
    private Long groupId;
    /**
     * 组
     **/
    @Column(columnDefinition = "LONGBLOB")
    private String group;
    /**
     * 角色
     **/
    @Column(length = 100)
    private String role;
    /**
     * 描述
     **/
    private String description;
    /**
     * 创建时间
     **/
    @TableField(fill= FieldFill.INSERT)
    private LocalDateTime gmtCreated;
    /**
     * 修改时间
     **/
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;

}
