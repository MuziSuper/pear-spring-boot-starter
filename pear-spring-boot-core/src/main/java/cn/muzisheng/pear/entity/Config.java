package cn.muzisheng.pear.entity;

import cn.muzisheng.pear.annotation.PearField;
import cn.muzisheng.pear.annotation.PearObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
@Data
@Entity(name = "config")
@NoArgsConstructor
@AllArgsConstructor
@TableName("config")
@PearObject(
        desc = "A configuration center for the pear system to configure static data for transfer to clients",
        path = "/config",
        pluralName = "Configs",
        group = "config"
)
public class Config {
    @Id
    @PearField(isPrimaryKey = true, isEdit = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 128, name = "`key`")
    @TableField(value = "`key`")
    @PearField
    private String key;
    @PearField
    @Column(length = 200)
    private String description;
    @PearField
    private Boolean autoload;
    @PearField
    private Boolean pub;
    @Column(length = 20)
    @PearField
    private String format;
    @PearField
    private String value;

}
