package cn.muzisheng.pear.entity;

import cn.muzisheng.pear.annotation.PearField;
import cn.muzisheng.pear.annotation.PearObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 用户组扩展信息
 **/
@Data
@Entity(name = "group_extra")
@TableName("group_extra")
@PearObject(
        desc = "Additional information of the user group.",
        path = "/groupExtra",
        pluralName = "groupExtras",
        group = "group"
)
public class GroupExtra {
    /**
     * 主键
     **/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @PearField(isPrimaryKey = true,isEdit = false)
    private Long id;
    /**
     * 组对象类型
     **/
    @Column(length = 128,name = "group_type")
    @TableId(value="group_type")
    @PearField
    private String groupType;
    /**
     * groupId
     **/
    @Column(name = "group_id")
    @TableField(value="group_id")
    @PearField(isUniqueKey = true)
    private Long groupId;
    /**
     * 键
     **/
    @Column(length = 128)
    @PearField
    private String key;
    /**
     * 值
     **/
    @PearField
    private String value;
}
