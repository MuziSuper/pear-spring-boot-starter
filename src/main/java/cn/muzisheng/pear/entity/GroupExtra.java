package cn.muzisheng.pear.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.*;
import lombok.Data;

/**
 * 用户组扩展信息
 **/
@Data
@Entity
@TableName("group_extra")
public class GroupExtra {
    /**
     * 主键
     **/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 组对象类型
     **/
    @Column(length = 128)
    private String objectType;
    /**
     * groupId
     **/
    private Long objectId;
    /**
     * 键
     **/
    @Column(name="`key`",length = 128)
    private String key;
    /**
     * 值
     **/
//    @Column(columnDefinition = "TEXT")
    private String value;
}
