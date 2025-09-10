package cn.muzisheng.pear;

import cn.muzisheng.pear.cache.LFUCache;
import cn.muzisheng.pear.cache.LRUCache;
import cn.muzisheng.pear.cache.RedisCache;
import cn.muzisheng.pear.config.CacheConfig;
import cn.muzisheng.pear.exception.CacheException;
import cn.muzisheng.pear.model.CacheStrategyEnum;
import io.jsonwebtoken.security.Keys;
import org.springframework.data.redis.core.KeyBoundCursor;
import org.springframework.data.redis.core.RedisTemplate;

import java.nio.Buffer;

/**
 * 缓存策略类，多种缓存的不同实现
 **/
public class CacheTemplate<K,V> {
    private CacheInterface<K,V> cache;
    private CacheStrategyEnum strategy;
    private long expire;
    private int capacity;
    private String cacheName;
    private RedisTemplate<String,Object> redisTemplate;
    private CacheTemplate(){}

    public static class Builder<K,V>{
        private final CacheTemplate<K,V> cacheTemplate=new CacheTemplate<K,V>();
        public Builder<K,V> cacheStrategy(CacheConfig config){
            cacheTemplate.cacheName = config.getCacheName();
            cacheTemplate.strategy = config.getStrategy();
            cacheTemplate.expire = config.getExpire();
            cacheTemplate.capacity = config.getCapacity();
            return this;
        }
        public Builder<K,V> cacheName(String cacheName){
            cacheTemplate.cacheName = cacheName;
            return this;
        }
        public Builder<K,V> strategy(CacheStrategyEnum strategy){
            cacheTemplate.strategy = strategy;
            return this;
        }
        public Builder<K,V> expire(long expire){
            cacheTemplate.expire = expire;
            return this;
        }
        public Builder<K,V> capacity(int capacity){
            cacheTemplate.capacity = capacity;
            return this;
        }
        public Builder<K,V> redisTemplate(RedisTemplate<String,Object> redisTemplate){
            cacheTemplate.redisTemplate = redisTemplate;
            return this;
        }
        public CacheTemplate<K,V> build(){
            if (cacheTemplate.cacheName.isEmpty()) {
                // 随机生成缓存名称
                cacheTemplate.cacheName = RandomStrUtil.getRandomStr(10);
            }
            if (cacheTemplate.capacity <= 0) {
                cacheTemplate.capacity=Constant.CACHE_DEFAULT_CAPACITY;
            }
            if (cacheTemplate.expire <= 0) {
                cacheTemplate.expire=Constant.CACHE_DEFAULT_EXPIRED;
            }
            if (Constant.CACHE_TYPE_LRU.equals(cacheTemplate.strategy.getStrategy())) {
                cacheTemplate.cache = new LRUCache<>(cacheTemplate.capacity, cacheTemplate.expire, cacheTemplate.cacheName);
            } else if (Constant.CACHE_TYPE_REDIS.equals(cacheTemplate.strategy.getStrategy())) {
                if(cacheTemplate.redisTemplate==null){
                    throw new CacheException("redisTemplate is null");
                }
                cacheTemplate.cache = new RedisCache<>(cacheTemplate.cacheName,cacheTemplate.redisTemplate);
            }else {
                cacheTemplate.cache = new LFUCache<>(cacheTemplate.capacity, cacheTemplate.expire, cacheTemplate.cacheName);
            }
            return cacheTemplate;
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

//    public static void main(String[] args) {
//        CacheConfig config= new CacheConfig.Builder().expire(1000).capacity(4).build();
//        CacheTemplate<Integer,Integer> cacheTemplate = new Builder<Integer, Integer>().cacheTemplate(config).build();
//        put(1, 1);
//        put(2, 2);
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        System.out.println(get(1));
//        System.out.println(get(2));
//        put(3, 3);
//        put(4, 4);
//        System.out.println(size());
//        System.out.println(cacheTemplate);
//        put(5, 5);
//        put(6, 6);
//        put(9, 9);
//        put(7, 7);
//        put(7, 8);
//        put(5, 3);
//        System.out.println(size());
//        System.out.println(cacheTemplate);
//        CacheTemplate<String,String> cacheStrategy2=new CacheTemplate
//                .Builder<String,String>()
//                .capacity(3)
//                .strategy(CacheStrategyEnum.LFU)
//                .build();
//        cacheStrategy2.put("1","1");
//        cacheStrategy2.put("2","2");
//        cacheStrategy2.put("3","3");
//        cacheStrategy2.put("4","4");
//        System.out.println(cacheStrategy2.size());
//    }
}
