package cn.muzisheng.pear.config.auto;

import cn.muzisheng.pear.config.UserConfig;
import cn.muzisheng.pear.properties.TokenProperties;
import cn.muzisheng.pear.properties.UserProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(UserProperties.class)
public class UserAutoConfiguration {
    @Bean
    public UserConfig defaultUserProperties(UserProperties properties) {
        UserConfig userConfig=new UserConfig();
        properties.applyTo(userConfig);
        return userConfig;
    }
}
