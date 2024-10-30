package cn.muzisheng.pear.entity;

import cn.muzisheng.pear.model.Profile;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@TableName("user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    // 主键
    @TableId
    private int ID;
    // 创建时间
    private Date gmtCreated;
    // 更新时间
    private Date gmtModified;
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
    private Date lastLogin;
    // 最后一次登陆ip
    private String lastLoginIP;
    // 用户来源渠道
    private String source;
    // 语言环境
    private String locale;
    // 时区
    private String timezone;
    // 用户信息
    private Profile profile;
    // 用户认证令牌
    private String token;
    public User(String username, String password){
        this.password = password;
        this.email = username;
    }
}
