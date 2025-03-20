package cn.muzisheng.pear.entity;

import cn.muzisheng.pear.annotation.PearField;
import cn.muzisheng.pear.annotation.PearObject;
import com.baomidou.mybatisplus.annotation.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.JdbcType;
import org.hibernate.annotations.Type;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 货品
 **/
@Entity
@Data
@Component
@TableName("product")
@PearObject(
        desc = "Product Information.",
        path = "/product",
        pluralName = "products"
)
public class Product {
    /**
     * 货品id
     **/
    @Id
    @PearField(isPrimaryKey = true,isRequire = true,isShow = true)
    private String id;
    /**
     * 货品名称
     **/
    @Column(length = 200)
    @PearField(isRequire = true,isShow = true,isSearchable = true,isEdit = true)
    private String name;
    /**
     * 用户组id
     **/
    @TableField(value ="groupId")
    @PearField(isRequire = true,isShow = true,isSearchable = true,isEdit = true,isUniqueKey = true)
    private Long groupId;
    /**
     * 是否启用
     **/
    @PearField(isRequire = true,isShow = true,isSearchable = true,isEdit = true)
    private Boolean enabled;
    /**
     * 货品模型
     **/
    @PearField(isRequire = true,isShow = true,isSearchable = true,isEdit = true)
    private byte[] model;
    /**
     * 创建时间
     **/
    @TableField(fill= FieldFill.INSERT,jdbcType = JdbcType.TIMESTAMP,value = "gmtCreated")
    @PearField(isRequire = true,isShow = true,isSearchable = true,isOrderable = true)
    private LocalDateTime gmtCreated;
    /**
     * 修改时间
     **/
    @PearField(isRequire = true,isShow = true,isSearchable = true,isOrderable = true)
    @TableField(fill= FieldFill.INSERT_UPDATE,value = "gmtModified")
    private LocalDateTime gmtModified;
}
