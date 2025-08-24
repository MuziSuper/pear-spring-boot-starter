package cn.muzisheng.pear.properties;

import cn.muzisheng.pear.config.CacheConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
/**
 * 缓存默认属性
 **/
@Data
@ConfigurationProperties("pear.cache.default")
public class CacheProperties {

    private long expire;
    private int capacity;
    private String strategy;
    private String cacheName;
    public void applyTo(CacheConfig config){
        if (this.expire > 0){
            config.setExpire(this.expire);
        }
        if (this.capacity > 0){
            config.setCapacity(this.capacity);
        }
        if (strategy != null&& !strategy.isEmpty()){
            config.setStrategy(strategy);
        }
        if (cacheName != null&& !cacheName.isEmpty()){
            config.setCacheName(cacheName);
        }

    }
}
