package cn.muzisheng.pear.config;

import cn.muzisheng.pear.Constant;
import cn.muzisheng.pear.model.CacheStrategyEnum;
import jakarta.annotation.PostConstruct;
import lombok.Data;
/**
 * 缓存配置类，允许开发人员自行注入CacheConfig实例，但只允许通过内部Builder类创建
 **/
@Data
public class CacheConfig {

    private long expire=Constant.CACHE_DEFAULT_EXPIRED;
    private int capacity=Constant.CACHE_DEFAULT_CAPACITY;
    private CacheStrategyEnum strategy=CacheStrategyEnum.strategyOf(Constant.CACHE_DEFAULT_STRATEGY);
    private String cacheName=Constant.CACHE_DEFAULT_NAME;
    private boolean enable=Constant.CACHE_DEFAULT_REDIS_ENABLE;
    private CacheConfig(){}
    @PostConstruct
    public void validate() {
        if(expire<=0){
            expire=Constant.CACHE_DEFAULT_EXPIRED;
        }
        if(capacity<=0){
            capacity=Constant.CACHE_DEFAULT_CAPACITY;
        }
        if (cacheName!=null&& !cacheName.trim().isEmpty()){
            cacheName=cacheName.trim();
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
        public Builder cacheName(String cacheName){
            cacheConfig.cacheName=cacheName;
            return this;
        }
        public Builder enable(boolean enable){
            cacheConfig.enable=enable;
            return this;
        }
        public CacheConfig build(){
            return cacheConfig;
        }
    }
}
