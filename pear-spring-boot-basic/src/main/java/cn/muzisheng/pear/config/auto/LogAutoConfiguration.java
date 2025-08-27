package cn.muzisheng.pear.config.auto;

import cn.muzisheng.pear.config.LogConfig;
import cn.muzisheng.pear.properties.LogProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * 日志条件化装配类
 **/
@Configuration
@EnableConfigurationProperties(LogProperties.class)
public class LogAutoConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(LogAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(LogConfig.class)
    public LogConfig defaultLogConfig(LogProperties properties) {
        LOG.info("LogConfig默认注册完成");
        LogConfig config = new LogConfig.Builder().build();
        properties.applyTo(config);
        return config;
    }
}
