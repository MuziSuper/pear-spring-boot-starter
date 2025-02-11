package cn.muzisheng.pear.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("config")
public class Config {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    private Long id;
    @Column(length = 128)
    @TableField("`key`")
    private String key;
    @Column(name="`description`",length = 200)
    private String description;
    private boolean autoload;
    private boolean pub;
    @Column(length = 20)
    private String format;
    @Column(name = "`value`")
    private String value;
}
