package cn.muzisheng.pear;

import cn.muzisheng.pear.cache.LFUCacheTemplate;
import cn.muzisheng.pear.cache.LRUCacheTemplate;
import cn.muzisheng.pear.cache.RedisCacheTemplate;
import cn.muzisheng.pear.config.CacheConfig;
import cn.muzisheng.pear.exception.CacheException;
import cn.muzisheng.pear.model.CacheStrategyEnum;
import jakarta.annotation.Nullable;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring6.context.SpringContextUtils;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

/**
 * 缓存工厂类，根据不同缓存配置创建缓存并存储在缓存容器中
 **/
@Component
public class CacheFactory {
    private final CacheConfig cacheConfig;
    private final RedisSupport redisSupport;

    @Autowired
    public CacheFactory(CacheConfig cacheConfig, @Nullable RedisSupport redisSupport){
        this.cacheConfig = cacheConfig;
        this.redisSupport = redisSupport;
    }

    /**
     * 创建缓存
     * @param expire 缓存过期时间，0表示不过期，单位秒
     * @param capacity 缓存容量，0表示无容量限制
     * @param strategy 缓存策略，默认使用LRU缓存策略
     * @param keyClass 缓存键Class对象
     * @param valueClass 缓存值Class对象
     * @param <K> 缓存键类型
     * @param <V> 缓存值类型
     * @return CacheTemplate对象
     * @throws CacheException 选择Redis缓存策略但未配置RedisTemplate对象
     **/
    public <K,V> CacheTemplate<K,V> createCacheTemplate(long expire, TimeUnit timeUnit, int capacity, CacheStrategyEnum strategy, Class<K> keyClass, Class<V> valueClass){
            if (capacity < 0) {
                capacity=cacheConfig.getCapacity();
            }
            if (expire < 0) {
                expire=cacheConfig.getExpire();
            }
            if (Constant.CACHE_TYPE_LRU.equals(strategy.getStrategy())) {
                return new LRUCacheTemplate<>(capacity, expire,timeUnit, keyClass, valueClass);
            } else if (Constant.CACHE_TYPE_REDIS.equals(strategy.getStrategy())) {
                if(redisSupport==null||redisSupport.getRedisTemplate()==null){
                    throw new CacheException("redisTemplate is null.");
                }
                return new RedisCacheTemplate<>(redisSupport.getRedisTemplate(), keyClass, valueClass);
            }else {
                return new LFUCacheTemplate<>(capacity, expire,timeUnit ,keyClass, valueClass);
            }
    }
}
