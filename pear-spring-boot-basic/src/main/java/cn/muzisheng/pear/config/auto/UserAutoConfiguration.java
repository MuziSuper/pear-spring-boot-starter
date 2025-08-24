package cn.muzisheng.pear.config.auto;

import cn.muzisheng.pear.config.UserConfig;
import cn.muzisheng.pear.properties.TokenProperties;
import cn.muzisheng.pear.properties.UserProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(UserProperties.class)
public class UserAutoConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(UserAutoConfiguration.class);

    @Bean
    public UserConfig defaultUserProperties(UserProperties properties) {
        LOG.info("UserConfig默认注册完成");
        UserConfig userConfig=new UserConfig();
        properties.applyTo(userConfig);
        return userConfig;
    }
}
