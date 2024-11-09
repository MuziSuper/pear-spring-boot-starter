package cn.muzisheng.pear.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("user")
public class User {
    // 主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    private Long id;
    // 创建时间
    @TableField(fill= FieldFill.INSERT)
    private LocalDateTime gmtCreated;

    // 更新时间
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;

    // 邮箱
    private String email;
    // 密码
    private String password;
    // 电话
    private String phone;
    // 英文姓
    private String firstName;
    // 英文名
    private String lastName;
    // 用户显示名
    private String displayName;
    // 是否为管理员身份
    private boolean isSuperUser;
    // 是否为员工
    private boolean isStaff;
    // 状态是否正常
    private boolean enabled;
    // 是否激活
    private boolean activated;
    // 最后一次登陆时间
    private LocalDateTime lastLogin;
    // 最后一次登陆ip
    private String lastLoginIp;
    // 用户来源渠道
    private String source;
    // 语言环境
    private String locale;
    // 时区
    private String timezone;
    // 用户信息
    @Column(columnDefinition = "LONGBLOB")
    private String profile;
    // 用户认证令牌
    private String token;
    public User(String username, String password){
        this.password = password;
        this.email = username;
    }
}
