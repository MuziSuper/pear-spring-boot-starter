package cn.muzisheng.pear.entity;

import cn.muzisheng.pear.annotation.PearField;
import cn.muzisheng.pear.annotation.PearObject;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.JdbcType;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity(name = "user")
@NoArgsConstructor
@AllArgsConstructor
@Data
@lombok.EqualsAndHashCode(callSuper = false)
@TableName("`user`")
@PearObject(
        desc = "User Information.",
        path = "/user",
        pluralName = "users",
        group = "user"
)
public class User extends BaseEntity {
    // 主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @PearField(isPrimaryKey = true)
    private Long id;
    // 头像
    @Column
    @PearField(isShow = false)
    private String avatar;
    // 邮箱
    @Column(length = 128)
    @PearField
    private String email;
    // 密码
    @JsonIgnore
    @Column(length = 128)
    @PearField(isShow = false, isSearchable = false, isFilterable = false, isOrderable = false)
    private String password;
    // 电话
    @Column(length = 64)
    @PearField
    private String phone;
    // 英文姓
    @Column(length = 128,name = "first_name")
    @TableField(value = "first_name")
    @PearField
    private String firstName;
    // 英文名
    @Column(length = 128,name = "last_name")
    @TableField(value = "last_name")
    @PearField
    private String lastName;
    // 用户显示名
    @Column(length = 128,name = "display_name")
    @TableField(value = "display_name")
    @PearField
    private String displayName;
    // 状态是否正常
    @PearField
    private Boolean enabled;
    // 是否激活
    @PearField
    private Boolean activated;
    // 最后一次登陆时间
    @TableField(fill= FieldFill.UPDATE,value ="last_login")
    @PearField
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    // 最后一次登陆ip
    @JsonIgnore
    @Column(length = 128,name = "last_login_ip")
    @TableField(value = "last_login_ip")
    @PearField(isShow = false, isSearchable = false, isFilterable = false, isOrderable = false)
    private String lastLoginIp;
    // 用户来源渠道
    @JsonIgnore
    @Column(length = 64)
    @PearField(isShow = false, isSearchable = false, isFilterable = false, isOrderable = false)
    private String source;
    // 语言环境
    @Column(length = 20)
    @PearField
    private String locale;
    // 时区
    @Column(length = 200)
    @PearField
    private String timezone;
    // 用户信息
    @JsonIgnore
    @PearField(isShow = false, isSearchable = false, isFilterable = false, isOrderable = false)
    private String profile;
    // 用户认证令牌
    @JsonIgnore
    @PearField(isShow = false, isEdit = false, isSearchable = false, isFilterable = false, isOrderable = false)
    private String token;
    // 创建时间
    @Column(name = "gmt_created")
    @TableField(fill= FieldFill.INSERT,value ="gmt_created")
    @PearField(isEdit = false)
    private LocalDateTime gmtCreated;

    // 更新时间
    @TableField(fill= FieldFill.INSERT_UPDATE,value ="gmt_modified")
    @PearField(isEdit = false)
    @Column(name = "gmt_modified")
    private LocalDateTime gmtModified;
    public User(String username, String password){
        this.password = password;
        this.email = username;
    }
}
