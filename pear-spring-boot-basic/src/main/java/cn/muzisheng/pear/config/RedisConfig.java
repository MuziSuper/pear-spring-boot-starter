package cn.muzisheng.pear.config;

import cn.muzisheng.pear.Constant;
import cn.muzisheng.pear.model.CacheStrategyEnum;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Data
public class RedisConfig {
    private long expire;
    public RedisConfig(CacheConfig config) {
        expire=config.getExpire();
    }
    @PostConstruct
    public void validate() {
        if(expire<=0){
            expire=Constant.CACHE_DEFAULT_EXPIRED;
        }
    }
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 设置key的序列化器
        template.setKeySerializer(new GenericJackson2JsonRedisSerializer());
        // 设置value的序列化器（使用Jackson2JsonRedisSerializer）
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        // 设置hash key和value的序列化器
        template.setHashKeySerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }
    // 配置Redis缓存
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // LRU缓存策略
        RedisCacheWriter cacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMillis(expire)) // 设置缓存过期时间
                .disableCachingNullValues() // 禁止缓存null值
                .computePrefixWith(cacheName -> cacheName + "::")
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager.builder(cacheWriter)
                .cacheDefaults(cacheConfiguration)
                .build();
    }
}
