package cn.muzisheng.pear.properties;

import cn.muzisheng.pear.constant.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@Configuration
@AllArgsConstructor
@ConfigurationProperties("app.user.password")
public class UserProperties {
    public String salt=Constant.APP_USER_PASSWORD_SALT;
}
