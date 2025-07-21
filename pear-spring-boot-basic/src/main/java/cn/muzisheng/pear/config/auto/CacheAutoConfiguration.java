package cn.muzisheng.pear.config.auto;

import cn.muzisheng.pear.config.CacheConfig;
import cn.muzisheng.pear.config.RedisConfig;
import cn.muzisheng.pear.properties.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CacheProperties.class)
public class CacheAutoConfiguration {
    /**
     * 默认配置Bean，当没有用户提供配置时生效
     */
    @Bean
    @ConditionalOnMissingBean(CacheConfig.class)
    public CacheConfig defaultCacheConfig(CacheProperties properties) {
        CacheConfig config = new CacheConfig(); // 使用默认值初始化
        properties.applyTo(config); // 应用属性配置覆盖
        return config;
    }
    /**
     * 当用户选择redis作为缓存系统时，向容器中注入cache的配置项
     **/
    @Bean
    @ConditionalOnBean(CacheConfig.class)
    @ConditionalOnProperty(name = "pear.cache.strategy", havingValue = "redis")
    public RedisConfig RedisCacheConfig(CacheConfig config) {
        return new RedisConfig(config);
    }


}
