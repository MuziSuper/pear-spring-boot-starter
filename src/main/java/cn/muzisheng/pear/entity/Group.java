package cn.muzisheng.pear.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

/**
 * 用户组
 **/
@Data
@Entity
@Embeddable
@TableName("`group`")
public class Group {
    /**
     * 用户组id
     **/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 用户组名称
     **/
    @Column(name="`name`",length = 200)
    private String name;
    /**
     * 用户组类型
     **/
    @Column(name="`type`",length = 24)
    private String type;
    /**
     * 用户组附加信息
     **/
    private String extra;
    /**
     * 用户组权限
     **/
//    @Column(columnDefinition = "LONGBLOB")
    private byte[] permission;
    /**
     * 创建时间
     **/
    @TableField(fill= FieldFill.INSERT)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime gmtCreated;
    /**
     * 修改时间
     **/
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;
}
