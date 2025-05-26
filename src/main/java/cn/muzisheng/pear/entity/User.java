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

@Entity
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
    // 创建时间
    @TableField(fill= FieldFill.INSERT,value ="gmtCreated")
    @PearField(isEdit = false)
    private LocalDateTime gmtCreated;

    // 更新时间
    @TableField(fill= FieldFill.INSERT_UPDATE,value ="gmtModified")
    @PearField
    private LocalDateTime gmtModified;
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
    @Column(length = 128)
    @TableField(value = "firstName")
    @PearField
    private String firstName;
    // 英文名
    @Column(length = 128)
    @TableField(value = "lastName")
    @PearField
    private String lastName;
    // 用户显示名
    @Column(length = 128)
    @TableField(value = "displayName")
    @PearField
    private String displayName;
    // 是否为管理员身份
    @TableField(value = "isSuperUser")
    @PearField
    private Boolean isSuperUser;
    // 是否为员工
    @TableField(value = "isStaff")
    @PearField
    private Boolean isStaff;
    // 状态是否正常
    @PearField
    private Boolean enabled;
    // 是否激活
    @PearField
    private Boolean activated;
    // 最后一次登陆时间
    @TableField(fill= FieldFill.UPDATE,value ="lastLogin")
    @PearField
    private LocalDateTime lastLogin;
    // 最后一次登陆ip
    @Column(length = 128)
    @TableField(value = "lastLoginIp")
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
    public User(String username, String password){
        this.password = password;
        this.email = username;
    }
}
