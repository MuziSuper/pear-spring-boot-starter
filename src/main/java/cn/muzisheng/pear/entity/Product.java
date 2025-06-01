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
@Entity(name = "product")
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
    @PearField(isPrimaryKey = true,isEdit = false)
    private String id;
    /**
     * 货品名称
     **/
    @Column(length = 200)
    @PearField
    private String name;
    /**
     * 用户组id
     **/
    @Column(name = "group_id")
    @TableField(value ="group_id")
    @PearField(isUniqueKey = true)
    private Long groupId;
    /**
     * 是否启用
     **/
    @PearField
    private Boolean enabled;
    /**
     * 货品模型
     **/
    @PearField(isOrderable = false, isSearchable = false,isFilterable = false)
    private byte[] model;
    /**
     * 创建时间
     **/
    @Column(name = "gmt_created")
    @TableField(fill= FieldFill.INSERT,jdbcType = JdbcType.TIMESTAMP,value = "gmt_created")
    @PearField(isEdit = false)
    private LocalDateTime gmtCreated;
    /**
     * 修改时间
     **/
    @PearField(isEdit = false)
    @Column(name = "gmt_modified")
    @TableField(fill= FieldFill.INSERT_UPDATE,value = "gmt_modified")
    private LocalDateTime gmtModified;
}
