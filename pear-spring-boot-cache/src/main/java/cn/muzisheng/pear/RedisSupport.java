package cn.muzisheng.pear;

import org.springframework.data.redis.core.RedisTemplate;

public interface RedisSupport {
    RedisTemplate<String, Object> getRedisTemplate();
}
