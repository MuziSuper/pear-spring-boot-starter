package cn.muzisheng.pear.config.auto;

import cn.muzisheng.pear.config.LogConfig;
import cn.muzisheng.pear.properties.LogProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(LogProperties.class)
public class LogAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(LogConfig.class)
    public LogConfig defaultLogConfig(LogProperties properties) {
        LogConfig config = new LogConfig();
        properties.applyTo(config);
        return config;
    }
}
