package cn.muzisheng.pear.config;

import cn.muzisheng.pear.Constant;
import cn.muzisheng.pear.model.CacheStrategyEnum;
import jakarta.annotation.PostConstruct;
import lombok.Data;

@Data
public class CacheConfig {

    private long expire=Constant.CACHE_DEFAULT_EXPIRED;
    private int capacity=Constant.CACHE_DEFAULT_CAPACITY;
    private String strategy=Constant.CACHE_DEFAULT_STRATEGY;
    private String cacheName=Constant.CACHE_DEFAULT_NAME;

    @PostConstruct
    public void validate() {
        if(expire<=0){
            expire=Constant.CACHE_DEFAULT_EXPIRED;
        }
        if(capacity<=0){
            capacity=Constant.CACHE_DEFAULT_CAPACITY;
        }
        if(CacheStrategyEnum.contains(strategy)){
            strategy=Constant.CACHE_DEFAULT_STRATEGY;
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
        public Builder strategy(String strategy){
            cacheConfig.strategy=strategy;
            return this;
        }
        public Builder cacheName(String cacheName){
            cacheConfig.cacheName=cacheName;
            return this;
        }
        public CacheConfig build(){
            return cacheConfig;
        }
    }
}
