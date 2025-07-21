package cn.muzisheng.pear.properties;

import cn.muzisheng.pear.Constant;
import cn.muzisheng.pear.config.UserConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties("pear.user")
public class UserProperties {
    private String passwordSalt;
    public void applyTo(UserConfig config){
        if(passwordSalt!=null){
            config.setPasswordSalt(passwordSalt);
        }
    }
}
