package cn.muzisheng.pear.entity;

import cn.muzisheng.pear.annotation.PearField;
import cn.muzisheng.pear.annotation.PearObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("config")
@Component
@PearObject(
        desc = "A configuration center for the pear system to configure static data for transfer to clients",
        path = "/config",
        pluralName = "Configs",
        group = "config"
)
public class Config {
    @Id
    @PearField(isPrimaryKey = true,isRequire = true,isShow = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 128)
    @TableField(value = "`key`")
    @PearField(isRequire = true,isShow = true,isSearchable = true,isEdit = true)
    private String key;
    @PearField(isRequire = true,isShow = true,isSearchable = true,isEdit = true)
    @Column(length = 200)
    private String description;
    @PearField(isRequire = true,isShow = true,isSearchable = true,isEdit = true)
    private boolean autoload;
    @PearField(isRequire = true,isShow = true,isSearchable = true,isEdit = true)
    private boolean pub;
    @Column(length = 20)
    @PearField(isRequire = true,isShow = true,isSearchable = true,isEdit = true)
    private String format;
    @PearField(isRequire = true,isShow = true,isSearchable = true,isEdit = true)
    private String value;
}
