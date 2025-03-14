package cn.muzisheng.pear.entity;

import cn.muzisheng.pear.annotation.PearField;
import cn.muzisheng.pear.annotation.PearObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 用户组扩展信息
 **/
@Component
@Data
@Entity
@TableName("group_extra")
@PearObject(
        desc = "Additional information of the user group.",
        path = "/groupExtra",
        pluralName = "groupExtras"
)
public class GroupExtra {
    /**
     * 主键
     **/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @PearField(isPrimaryKey = true,isRequire = true,isShow = true)
    private Long id;
    /**
     * 组对象类型
     **/
    @Column(length = 128)
    @PearField(isRequire = true,isShow = true,isSearchable = true,isEdit = true,isOrderable = true)
    private String groupType;
    /**
     * groupId
     **/
    @PearField(isUniqueKey = true,isRequire = true,isShow = true,isSearchable = true,isEdit = true,isOrderable = true)
    private Long groupId;
    /**
     * 键
     **/
    @Column(name="`key`",length = 128)
    @PearField(isRequire = true,isShow = true,isSearchable = true,isOrderable = true)
    private String key;
    /**
     * 值
     **/
    @PearField(isRequire = true,isShow = true,isSearchable = true,isOrderable = true)
    private String value;
}
