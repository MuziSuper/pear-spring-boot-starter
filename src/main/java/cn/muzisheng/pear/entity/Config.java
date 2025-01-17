package cn.muzisheng.pear.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
    @Column(name="`key`",length = 128)
    private String key;
    @Column(name="`desc`",length = 200)
    private String desc;
    private boolean autoload;
    private boolean pub;
    @Column(length = 20)
    private String format;
    @Column(name = "`value`")
    private String value;
}
