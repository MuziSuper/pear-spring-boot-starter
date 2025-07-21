package cn.muzisheng.pear.entity;

import cn.muzisheng.pear.annotation.PearField;
import cn.muzisheng.pear.annotation.PearObject;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 组成员
 **/
@Data
@Entity(name = "`group_member`")
@TableName("group_member")
@PearObject(
        desc = "Group member information, including member permissions.",
        path = "/groupMember",
        pluralName = "groupMembers",
        group = "group"

)
public class GroupMember {
    /**
     * 组成员id
     **/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @PearField(isPrimaryKey = true)
    private Long id;
    /**
     * 用户id
     **/
    @Column(name = "user_id")
    @TableField(value = "user_id")
    @PearField
    private Long userId;
    /**
     * 用户
     **/
    @Transient
    private User user;
    /**
     * 组id
     **/
    @Column(name = "group_id")
    @TableField(value = "group_id")
    @PearField
    private Long groupId;
    /**
     * 组
     **/
    @Transient
    private Group group;
    /**
     * 角色
     **/
    @Column(length = 100)
    @PearField
    private String role;
    /**
     * 描述
     **/
    @PearField
    private String description;
    /**
     * 创建时间
     **/
    @Column(name = "gmt_created")
    @PearField(isEdit = false)
    @TableField(fill= FieldFill.INSERT,value = "gmt_created")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime gmtCreated;
    /**
     * 修改时间
     **/
    @Column(name = "gmt_modified")
    @TableField(fill= FieldFill.INSERT_UPDATE,value = "gmt_modified")
    @Temporal(TemporalType.TIMESTAMP)
    @PearField(isEdit = false)
    private LocalDateTime gmtModified;

}
