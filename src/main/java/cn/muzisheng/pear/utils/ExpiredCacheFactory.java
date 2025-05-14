package cn.muzisheng.pear.utils;

import cn.muzisheng.pear.model.ExpiredCacheValue;

import java.util.LinkedHashMap;

public class ExpiredCacheFactory{
    private static volatile ExpiredCache<?,?> expiredCache;
    private final static Object lock = new Object();

    /**
     * 创建一个缓存容器
     *
     * @param expireTime 缓存过期时间
     **/
    public static <K,V> ExpiredCache<K,V> newExpiredCacheFactory(int capacity, long expireTime) {
        if(expiredCache==null){
            synchronized (lock){
                if(expiredCache==null) {
                    LinkedHashMap<K, ExpiredCacheValue<V>> cache = new LinkedHashMap<>(capacity, 0.75f, true);
                    expiredCache = new ExpiredCache<>(cache, expireTime);
                }
            }
        }
        return (ExpiredCache<K, V>) expiredCache;
    }
}
