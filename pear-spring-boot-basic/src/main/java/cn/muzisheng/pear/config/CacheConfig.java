package cn.muzisheng.pear.config;

import cn.muzisheng.pear.Constant;
import cn.muzisheng.pear.model.CacheStrategyEnum;
import jakarta.annotation.PostConstruct;
import lombok.Data;

import java.util.concurrent.TimeUnit;

/**
 * 缓存配置类，允许开发人员自行注入CacheConfig实例，但只允许通过内部Builder类创建
 **/
@Data
public class CacheConfig {
    private long expire=Constant.CACHE_DEFAULT_EXPIRED;
    private TimeUnit timeUnit=Constant.CACHE_DEFAULT_TIME_UNIT;
    private int capacity=Constant.CACHE_DEFAULT_CAPACITY;
    private CacheStrategyEnum strategy=CacheStrategyEnum.strategyOf(Constant.CACHE_DEFAULT_STRATEGY);
    private CacheConfig(){}
    @PostConstruct
    public void validate() {
        if(expire<0){
            expire=Constant.CACHE_DEFAULT_EXPIRED;
        }
        if(capacity<0){
            capacity=Constant.CACHE_DEFAULT_CAPACITY;
        }
        if(strategy==null){
            strategy=CacheStrategyEnum.strategyOf(Constant.CACHE_DEFAULT_STRATEGY);
        }
        if(timeUnit==null){
            timeUnit=Constant.CACHE_DEFAULT_TIME_UNIT;
        }
    }
    public static class Builder{
        private final CacheConfig cacheConfig=new CacheConfig();
        public Builder expire(long expire){
            cacheConfig.expire=expire;
            return this;
        }
        public Builder capacity(int capacity){
            cacheConfig.capacity=capacity;
            return this;
        }
        public Builder strategy(CacheStrategyEnum strategy){
            cacheConfig.strategy=strategy;
            return this;
        }
        public CacheConfig build(){
            return cacheConfig;
        }
    }
}
