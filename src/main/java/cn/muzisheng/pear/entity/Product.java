package cn.muzisheng.pear.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 货品
 **/
@Entity
@Data
@TableName("product")
public class Product {
    /**
     * 货品id
     **/
    @Id
    private String id;
    /**
     * 货品名称
     **/
    @Column(length = 200)
    private String name;
    /**
     * 用户组id
     **/
    private Long groupId;
    /**
     * 是否启用
     **/
    private Boolean enabled;
    /**
     * 货品模型
     **/
    @Column(columnDefinition = "LONGBLOB")
    private String model;
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
