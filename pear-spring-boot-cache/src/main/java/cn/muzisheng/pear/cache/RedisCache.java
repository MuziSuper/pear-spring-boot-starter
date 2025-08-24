package cn.muzisheng.pear.cache;

import cn.muzisheng.pear.CacheInterface;
import cn.muzisheng.pear.config.CacheConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import javax.print.attribute.standard.JobKOctets;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class RedisCache<K,V> implements CacheInterface<K,V> {
    private final RedisTemplate<String,Object> redisTemplate;
    private final static Logger LOG= LoggerFactory.getLogger(RedisCache.class);
    private final ReadWriteLock lock=new ReentrantReadWriteLock();
    private final Lock writeLock=lock.writeLock();
    private final Lock readLock=lock.readLock();
    private final String CACHE_KEY;
    public RedisCache(CacheConfig config,RedisTemplate<String,Object> redisTemplate){
        CACHE_KEY=config.getCacheName();
        this.redisTemplate=redisTemplate;
    }


    @Override
    public V get(K key) {
        try{
            readLock.lock();
            Object value= redisTemplate.opsForHash().get(CACHE_KEY,key);
            try {
                return (V) value;
            }catch (ClassCastException e){
                LOG.warn("Data type error in the cache system");
                return null;
            }
        }finally {
            readLock.unlock();
        }
    }

    @Override
    public void put(K key, V value) {
        try{
            writeLock.lock();
            redisTemplate.opsForHash().put(CACHE_KEY,key,value);
        }finally {
            writeLock.unlock();
        }
    }

    @Override
    public void remove(K key) {
        try{
           writeLock.lock();
           redisTemplate.opsForHash().delete(CACHE_KEY,key);
        }finally {
            writeLock.unlock();
        }

    }

    @Override
    public Long size() {
        try {
            readLock.lock();
            return redisTemplate.opsForHash().size(CACHE_KEY);
        }finally {
            readLock.unlock();
        }
    }

    @Override
    public void clear() {
        try {
            writeLock.lock();
            redisTemplate.opsForHash().delete(CACHE_KEY);
        }finally {
            writeLock.unlock();
        }

    }

    @Override
    public boolean containsKey(K key) {
        try {
            readLock.lock();
            return redisTemplate.opsForHash().hasKey(CACHE_KEY, key);
        }finally {
            readLock.unlock();
        }
    }
}
