package cn.muzisheng.pear.autoConfig;

import cn.muzisheng.pear.Constant;
import cn.muzisheng.pear.cache.RedisCache;
import cn.muzisheng.pear.config.CacheConfig;
import cn.muzisheng.pear.config.RedisConfig;
import cn.muzisheng.pear.properties.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.awt.image.Kernel;

@Configuration
public class RedisAutoConfiguration {
    /**
     * 当用户选择redis作为缓存系统时，向容器中注入cache的配置项
     **/
    @Bean
    @ConditionalOnBean({RedisConfig.class, CacheConfig.class})
    public RedisCache<?,?> RedisCacheConfig(CacheConfig config) {
        return new RedisCache<>(config);
    }

}
