package cn.muzisheng.pear;

import cn.muzisheng.pear.cache.LFUCache;
import cn.muzisheng.pear.cache.LRUCache;
import cn.muzisheng.pear.cache.RedisCache;
import cn.muzisheng.pear.config.CacheConfig;
import cn.muzisheng.pear.model.CacheStrategyEnum;
import net.bytebuddy.implementation.bytecode.assign.TypeCasting;
import org.hibernate.Cache;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class CacheStrategy<K, V> {
    private CacheInterface<K, V> cache;
    private CacheStrategyEnum strategy;
    private long expire;
    private int capacity;
    private String cacheName;
    private CacheStrategy() {
    }
    private CacheStrategy(CacheStrategyEnum strategy,int capacity, long expire, String cacheName){
        this.strategy = strategy;
        this.capacity = capacity;
        this.cacheName = cacheName;
        this.expire = expire;
    }
    public static class Builder<K,V> {
        private final AtomicInteger count=new AtomicInteger(0);
        private final CacheStrategy<K,V> cacheStrategy=new CacheStrategy<>();
        public Builder<K,V> cacheStrategy(CacheConfig config){
            if(config.getStrategy().equals(CacheStrategyEnum.LRU.getStrategy())){
                cacheStrategy.cache = new LRUCache<>(config);
            }else if(config.getStrategy().equals(CacheStrategyEnum.LFU.getStrategy())){
                cacheStrategy.cache = new LFUCache<>(config);
            }else if(config.getStrategy().equals(CacheStrategyEnum.REDIS.getStrategy())){
                cacheStrategy.cache = new RedisCache<>(config);
            }
            return this;
        }
        public Builder<K,V> cacheName(String cacheName){
            cacheStrategy.cacheName = cacheName;
            return this;
        }
        public Builder<K,V> expire(long expire){
            cacheStrategy.expire = expire;
            return this;
        }
        public Builder<K,V> capacity(int capacity){
            cacheStrategy.capacity = capacity;
            return this;
        }
        public Builder<K,V> strategy(CacheStrategyEnum strategy){
            cacheStrategy.strategy = strategy;
            return this;
        }

        public CacheStrategy<K,V> build(){
            if (cacheStrategy.cacheName == null){
                cacheStrategy.cacheName = Constant.CACHE_DEFAULT_NAME+ count.incrementAndGet();
            }
            // 缓存已存在即已经由配置自动创建pear缓存系统
            if(cacheStrategy.cache!= null){
                return cacheStrategy;
            }
            // 用户自定义缓存
            if(cacheStrategy.strategy == null){
                cacheStrategy.strategy = CacheStrategyEnum.strategyOf(Constant.CACHE_DEFAULT_STRATEGY);
            }

            if (cacheStrategy.capacity<=0){
                cacheStrategy.capacity = Constant.CACHE_DEFAULT_CAPACITY;
            }
            if (cacheStrategy.expire<=0){
                cacheStrategy.expire = Constant.CACHE_DEFAULT_EXPIRED;
            }
            return new CacheStrategy<>(cacheStrategy.strategy,cacheStrategy.capacity,cacheStrategy.expire,cacheStrategy.cacheName);
        }
    }

    public V get(K key) {
        return cache.get(key);
    }

    public void put(K key, V value) {
        cache.put(key, value);
    }

    public void remove(K key) {
        cache.remove(key);
    }

    public void clear() {
        cache.clear();
    }

    public Long size() {
        return cache.size();
    }

    public boolean containsKey(K key) {
        return cache.containsKey(key);
    }

    public String toString() {
        return cache.toString();
    }

    public static void main(String[] args) {
        CacheConfig config= new CacheConfig.Builder().expire(1000).capacity(4).build();
        CacheStrategy<Integer,Integer> cacheStrategy = new CacheStrategy.Builder<Integer, Integer>().cacheStrategy(config).build();
        cacheStrategy.put(1, 1);
        cacheStrategy.put(2, 2);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(cacheStrategy.get(1));
        System.out.println(cacheStrategy.get(2));
        cacheStrategy.put(3, 3);
        cacheStrategy.put(4, 4);
        System.out.println(cacheStrategy.size());
        System.out.println(cacheStrategy);
        cacheStrategy.put(5, 5);
        cacheStrategy.put(6, 6);
        cacheStrategy.put(9, 9);
        cacheStrategy.put(7, 7);
        cacheStrategy.put(7, 8);
        cacheStrategy.put(5, 3);
        System.out.println(cacheStrategy.size());
        System.out.println(cacheStrategy);
        CacheStrategy<String,String> cacheStrategy2=new CacheStrategy
                .Builder<String,String>()
                .capacity(3)
                .strategy(CacheStrategyEnum.LFU)
                .build();
        cacheStrategy2.put("1","1");
        cacheStrategy2.put("2","2");
        cacheStrategy2.put("3","3");
        cacheStrategy2.put("4","4");
        System.out.println(cacheStrategy2.size());
    }
}
