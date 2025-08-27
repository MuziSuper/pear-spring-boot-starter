package cn.muzisheng.pear.config.auto;

import cn.muzisheng.pear.conditional.OnCacheEnabledCondition;
import cn.muzisheng.pear.config.CacheConfig;
import cn.muzisheng.pear.config.RedisConfig;
import cn.muzisheng.pear.properties.CacheProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.connection.RedisConnectionFactory;
/**
 * 缓存条件化装配类
 **/
@Configuration
@EnableConfigurationProperties(CacheProperties.class)
public class CacheAutoConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(CacheAutoConfiguration.class);

    /**
     * 当用户没有注入CacheConfig时生效
     */
    @Bean
    @ConditionalOnMissingBean(CacheConfig.class)
    public CacheConfig defaultCacheConfig(CacheProperties properties) {
        LOG.info("CacheConfig默认注册完成");
        CacheConfig config = new CacheConfig.Builder().build(); // 使用默认值初始化
        properties.applyTo(config); // 应用属性配置覆盖
        return config;
    }
    /**
     * 当用户选择redis作为缓存系统且存在Redis工厂且选择启用pear的redis配置（默认）时，向容器中注入RedisConfig
     **/
    @Bean
    @Conditional(OnCacheEnabledCondition.class)
    @ConditionalOnBean({CacheConfig.class, RedisConnectionFactory.class})
    public RedisConfig RedisCacheConfig(CacheConfig config) {
        LOG.info("RedisConfig默认注册完成");
        return new RedisConfig(config);
    }


}
