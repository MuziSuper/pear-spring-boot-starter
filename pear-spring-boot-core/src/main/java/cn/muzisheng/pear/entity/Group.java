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
 * 用户组
 **/
@Data
@Entity(name = "`group`")
@TableName("`group`")
@PearObject(
        desc = "User group, it is possible to group users.",
        path = "/group",
        pluralName = "groups"
)
public class Group {
    /**
     * 用户组id
     **/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @PearField(isPrimaryKey = true,isEdit = false)
    private Long id;
    /**
     * 用户组名称
     **/
    @PearField
    @Column(length = 200)
    private String name;
    /**
     * 用户组类型
     **/
    @PearField
    @Column(length = 24)
    private String type;
    /**
     * 用户组附加信息
     **/
    @PearField
    private String extra;
    /**
     * 用户组权限
     **/
    @PearField(isFilterable = false, isOrderable = false, isSearchable = false)
    private byte[] permission;
    /**
     * 创建时间
     **/
    @TableField(fill= FieldFill.INSERT,value = "gmt_created")
    @Column(name = "gmt_created")
    @PearField(isEdit = false)
    private LocalDateTime gmtCreated;
    /**
     * 修改时间
     **/
    @TableField(fill= FieldFill.INSERT_UPDATE,value = "gmt_modified")
    @Column(name = "gmt_modified")
    @PearField(isEdit = false)
    private LocalDateTime gmtModified;
}
