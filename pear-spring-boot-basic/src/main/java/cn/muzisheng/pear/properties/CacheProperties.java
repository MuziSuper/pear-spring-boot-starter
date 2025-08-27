package cn.muzisheng.pear.properties;

import cn.muzisheng.pear.config.CacheConfig;
import cn.muzisheng.pear.model.CacheStrategyEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
/**
 * 缓存默认属性
 **/
@Data
@ConfigurationProperties("pear.starter.cache")
public class CacheProperties {

    private long expire;
    private int capacity;
    private String strategy;
    private String cacheName;
    private Redis redis=new Redis();
    // 采用Redis缓存是否启用Pear框架默认配置
    public static class Redis {
        private boolean enable;
    }
    /**
     * 将properties属性应用到CacheConfig中
     **/
    public void applyTo(CacheConfig config){
        if (this.expire > 0){
            config.setExpire(this.expire);
        }
        if (this.capacity > 0){
            config.setCapacity(this.capacity);
        }
        if (strategy != null&& !strategy.isEmpty()&& CacheStrategyEnum.contains(strategy)){
            config.setStrategy(CacheStrategyEnum.strategyOf( strategy));
        }
        if (cacheName != null&& !cacheName.isEmpty()){
            config.setCacheName(cacheName);
        }
        config.setEnable(this.redis.enable);

    }
}
