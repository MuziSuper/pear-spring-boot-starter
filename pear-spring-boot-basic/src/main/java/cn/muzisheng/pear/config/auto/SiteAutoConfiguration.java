package cn.muzisheng.pear.config.auto;

import cn.muzisheng.pear.config.SiteConfig;
import cn.muzisheng.pear.properties.SiteProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SiteProperties.class)
public class SiteAutoConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(SiteAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(SiteConfig.class)
    public SiteConfig defaultConfigProperties(SiteProperties properties) {
        LOG.info("SiteConfig默认注入完成");
        SiteConfig config = new SiteConfig(); // 使用默认值初始化
        properties.applyTo(config); // 应用属性配置覆盖
        return config;
    }

}
