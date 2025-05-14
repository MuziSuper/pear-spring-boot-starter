package cn.muzisheng.pear.entity;

import cn.muzisheng.pear.annotation.PearField;
import cn.muzisheng.pear.annotation.PearObject;
import com.baomidou.mybatisplus.annotation.*;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 用户组
 **/
@Data
@Entity
@Component
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
    @PearField(isPrimaryKey = true,isRequire = true,isShow = true)
    private Long id;
    /**
     * 用户组名称
     **/
    @PearField(isRequire = true,isShow = true,isSearchable = true,isEdit = true)
    @Column(length = 200)
    private String name;
    /**
     * 用户组类型
     **/
    @PearField(isRequire = true,isShow = true,isSearchable = true,isEdit = true)
    @Column(length = 24)
    private String type;
    /**
     * 用户组附加信息
     **/
    @PearField(isRequire = true,isShow = true,isSearchable = true,isEdit = true)
    private String extra;
    /**
     * 用户组权限
     **/
    @PearField(isRequire = true,isShow = true,isSearchable = true,isEdit = true)
    private byte[] permission;
    /**
     * 创建时间
     **/
    @TableField(fill= FieldFill.INSERT,value = "gmtCreated")
    @Temporal(TemporalType.TIMESTAMP)
    @PearField(isRequire = true,isShow = true,isSearchable = true,isEdit = true,isOrderable = true)
    private LocalDateTime gmtCreated;
    /**
     * 修改时间
     **/
    @TableField(fill= FieldFill.INSERT_UPDATE,value = "gmtModified")
    @PearField(isRequire = true,isShow = true,isSearchable = true,isEdit = true,isOrderable = true)
    private LocalDateTime gmtModified;
}
