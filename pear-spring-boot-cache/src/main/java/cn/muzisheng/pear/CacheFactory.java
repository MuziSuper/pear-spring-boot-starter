package cn.muzisheng.pear;

import cn.muzisheng.pear.config.CacheConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;

public class CacheFactory {
    private final CacheConfig config;
    private final Optional<RedisTemplate<String,Object>> optionalRedisTemplate;
    public CacheFactory(CacheConfig config, Optional<RedisTemplate<String,Object>> optionalRedisTemplate){
        this.config = config;
        this.optionalRedisTemplate = optionalRedisTemplate;
    }
    public <K,V>CacheStrategy<K,V> makeCache(){

    }
}
