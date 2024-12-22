package cn.muzisheng.pear.utils;

import cn.muzisheng.pear.model.ExpiredCacheValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpiredCache<K,V> {
    private ConcurrentHashMap<K, ExpiredCacheValue<V>> cache;
    private long expired;

    public ExpiredCache<K,V> newExpiredCache(long expireTime){
        ConcurrentHashMap<K, ExpiredCacheValue<V>> cache = new ConcurrentHashMap<>();
        return new ExpiredCache<>(cache, expireTime);
    }

    /**
     * 获取缓存中保存的值，未命中则返回null
     */
    public V get(K key){
        ExpiredCacheValue<V> value=this.cache.get(key);
        if (value!=null){
            if(LocalDateTime.now().isBefore(value.getLastTime().plusSeconds(expired))){
                return value.getVal();
            }
            this.cache.remove(key);
        }
        return null;
    }

    // 添加缓存
    public void add(K key, V value){
        ExpiredCacheValue<V> expiredCacheValue=new ExpiredCacheValue<>(LocalDateTime.now(),value);
        this.cache.put(key,expiredCacheValue);
    }

    // 判断缓存中是否存在该键
    public boolean contains(K key){
        return this.cache.containsKey(key);
    }

    // 删除缓存
    public boolean remove(K key){
        return this.cache.remove(key)!=null;
    }
}
