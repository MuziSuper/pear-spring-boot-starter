package cn.muzisheng.pear.config.auto;

import cn.muzisheng.pear.config.UserConfig;
import cn.muzisheng.pear.properties.UserProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * 用户盐条件化装配类
 **/
@Configuration
@EnableConfigurationProperties(UserProperties.class)
public class UserAutoConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(UserAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(UserConfig.class)
    public UserConfig defaultUserProperties(UserProperties properties) {
        LOG.info("UserConfig默认注册完成");
        UserConfig userConfig=new UserConfig.Builder().build();
        properties.applyTo(userConfig);
        return userConfig;
    }
}
