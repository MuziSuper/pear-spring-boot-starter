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
    private Long id;
    /**
     * 组对象类型
     **/
    @Column(length = 128)
    private String groupType;
    /**
     * groupId
     **/
    private Long groupId;
    /**
     * 键
     **/
    @Column(name="`key`",length = 128)
    private String key;
    /**
     * 值
     **/
    private String value;
}
