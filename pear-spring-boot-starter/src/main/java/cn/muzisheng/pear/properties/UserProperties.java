package cn.muzisheng.pear.properties;

import cn.muzisheng.pear.constant.Constant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("app.user.password.salt")
public class UserProperties {
    public String salt=Constant.APP_USER_PASSWORD_SALT;
}
