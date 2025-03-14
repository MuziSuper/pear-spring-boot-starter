package cn.muzisheng.pear.entity;

import cn.muzisheng.pear.annotation.PearField;
import cn.muzisheng.pear.annotation.PearObject;
import com.baomidou.mybatisplus.annotation.*;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 组成员
 **/
@Data
@Component
@Entity
@TableName("group_member")
@PearObject(
        desc = "Group member information, including member permissions.",
        path = "/groupMember",
        pluralName = "groupMembers"
)
public class GroupMember {
    /**
     * 组成员id
     **/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @PearField(isPrimaryKey = true,isRequire = true,isShow = true)
    private Long id;
    /**
     * 用户id
     **/
//    private Long userId;
    /**
     * 用户
     **/
    @ManyToOne
    @PearField(isRequire = true,isShow = true,isSearchable = true,isEdit = true,isUniqueKey = true)
    @JoinColumn(name = "userId")
    private User user;
    /**
     * 组id
     **/
//    private Long groupId;
    /**
     * 组
     **/
    @ManyToOne
    @JoinColumn(name = "groupId")
    @PearField(isRequire = true,isShow = true,isSearchable = true,isEdit = true,isUniqueKey = true)
    private Group group;
    /**
     * 角色
     **/
    @Column(length = 100)
    @PearField(isRequire = true,isShow = true,isSearchable = true,isEdit = true)
    private String role;
    /**
     * 描述
     **/
    @PearField(isRequire = true,isShow = true,isSearchable = true,isEdit = true)
    private String description;
    /**
     * 创建时间
     **/
    @PearField(isRequire = true,isShow = true,isSearchable = true,isOrderable = true)
    @TableField(fill= FieldFill.INSERT)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime gmtCreated;
    /**
     * 修改时间
     **/
    @TableField(fill= FieldFill.INSERT_UPDATE)
    @Temporal(TemporalType.TIMESTAMP)
    @PearField(isRequire = true,isShow = true,isSearchable = true,isOrderable = true)
    private LocalDateTime gmtModified;

}
