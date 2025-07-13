package cn.muzisheng.pear.entity;

import cn.muzisheng.pear.annotation.PearField;
import cn.muzisheng.pear.annotation.PearObject;
import com.baomidou.mybatisplus.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity(name = "user")
@NoArgsConstructor
@AllArgsConstructor
@Component
@Data
@TableName("`user`")
@PearObject(
        desc = "User Information.",
        path = "/user",
        pluralName = "users",
        group = "user"
)
public class User{
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
    @Column(length = 128)
    @PearField(isShow = false,isSearchable = false)
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
    // 是否为管理员身份
    @TableField(value = "is_superUser")
    @PearField
    @Column(name = "is_superUser")
    private Boolean isSuperUser;
    // 是否为员工
    @TableField(value = "is_staff")
    @PearField
    @Column(name = "is_staff")
    private Boolean isStaff;
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
    @Column(length = 128,name = "last_login_ip")
    @TableField(value = "last_login_ip")
    @PearField
    private String lastLoginIp;
    // 用户来源渠道
    @Column(length = 64)
    @PearField
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
    @PearField
    private String profile;
    // 用户认证令牌
    @PearField
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
