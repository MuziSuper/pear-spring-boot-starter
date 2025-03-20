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
    @PearField(isPrimaryKey = true,isRequire = true,isFilterable = true,isShow = true)
    private Long id;
    // 创建时间
    @TableField(fill= FieldFill.INSERT,value ="gmtCreated")
    @PearField(isRequire = true,isShow = true,isFilterable = true,isSearchable = true,isOrderable = true)
    private LocalDateTime gmtCreated;

    // 更新时间
    @TableField(fill= FieldFill.INSERT_UPDATE,value ="gmtModified")
    @PearField(isRequire = true,isShow = true,isSearchable = true,isFilterable = true,isEdit = true,isOrderable = true)
    private LocalDateTime gmtModified;
    // 邮箱
    @Column(length = 128)
    @PearField(isRequire = true,isShow = true,isSearchable = true,isFilterable = true,isEdit = true)
    private String email;
    // 密码
    @Column(length = 128)
    @PearField(isRequire = true)
    private String password;
    // 电话
    @Column(length = 64)
    @PearField(isRequire = true,isShow = true,isFilterable = true,isSearchable = true,isEdit = true)
    private String phone;
    // 英文姓
    @Column(length = 128)
    @TableField(value = "firstName")
    @PearField(isRequire = true,isShow = true,isFilterable = true,isSearchable = true,isEdit = true)
    private String firstName;
    // 英文名
    @Column(length = 128)
    @TableField(value = "lastName")
    @PearField(isRequire = true,isShow = true,isFilterable = true,isSearchable = true,isEdit = true)
    private String lastName;
    // 用户显示名
    @Column(length = 128)
    @TableField(value = "displayName")
    @PearField(isRequire = true,isShow = true,isFilterable = true,isSearchable = true,isEdit = true)
    private String displayName;
    // 是否为管理员身份
    @TableField(value = "isSuperUser")
    @PearField(isRequire = true,isShow = true,isFilterable = true,isSearchable = true,isEdit = true)
    private boolean isSuperUser;
    // 是否为员工
    @TableField(value = "isStaff")
    @PearField(isRequire = true,isShow = true,isFilterable = true,isSearchable = true,isEdit = true)
    private boolean isStaff;
    // 状态是否正常
    @PearField(isRequire = true,isShow = true,isFilterable = true,isSearchable = true,isEdit = true)
    private boolean enabled;
    // 是否激活
    @PearField(isRequire = true,isShow = true,isFilterable = true,isSearchable = true,isEdit = true)
    private boolean activated;
    // 最后一次登陆时间
    @TableField(fill= FieldFill.UPDATE,value ="lastLogin")
    @PearField(isRequire = true,isShow = true,isFilterable = true,isSearchable = true,isEdit = true)
    private LocalDateTime lastLogin;
    // 最后一次登陆ip
    @Column(length = 128)
    @TableField(value = "lastLoginIp")
    @PearField(isRequire = true,isShow = true,isFilterable = true,isSearchable = true,isEdit = true)
    private String lastLoginIp;
    // 用户来源渠道
    @Column(length = 64)
    @PearField(isRequire = true,isShow = true,isFilterable = true,isSearchable = true,isEdit = true)
    private String source;
    // 语言环境
    @Column(length = 20)
    @PearField(isRequire = true,isShow = true,isFilterable = true,isSearchable = true,isEdit = true)
    private String locale;
    // 时区
    @Column(length = 200)
    @PearField(isRequire = true,isShow = true,isFilterable = true,isSearchable = true,isEdit = true)
    private String timezone;
    // 用户信息
//    @Column(columnDefinition = "TEXT")
    @PearField(isRequire = true,isShow = true,isFilterable = true,isSearchable = true,isEdit = true)
    private String profile;
    // 用户认证令牌
    @PearField(isRequire = true,isShow = true,isFilterable = true,isSearchable = true,isEdit = true)
    private String token;
    public User(String username, String password){
        this.password = password;
        this.email = username;
    }
}
