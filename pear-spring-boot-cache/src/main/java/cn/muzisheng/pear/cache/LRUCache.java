package cn.muzisheng.pear.cache;

import cn.muzisheng.pear.CacheInterface;
import cn.muzisheng.pear.config.CacheConfig;
import cn.muzisheng.pear.model.ExpiredCacheValue;

import java.util.LinkedHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
/**
 * LRU缓存, 基于LinkedHashMap实现, 使用读写锁保证线程安全，超过最大容量后，最近最久未使用的数据会被清除
 **/
public class LRUCache<K,V> implements CacheInterface<K,V> {
    private final LinkedHashMap<K, ExpiredCacheValue<V>> cache;
    private final ReadWriteLock readWriteLock=new ReentrantReadWriteLock();
    private final Lock readLock=readWriteLock.readLock();
    private final Lock writeLock=readWriteLock.writeLock();
    private final long expire;

    public LRUCache(int capacity, long expire, String cacheName) {
        this.expire = expire;
        cache = new LinkedHashMap<>(capacity,0.75f,true){
            @Override
            protected boolean removeEldestEntry(java.util.Map.Entry<K,ExpiredCacheValue<V>> eldest) {
                return size()>capacity;
            }
        };
    }
    public LRUCache(CacheConfig config) {
        this.expire = config.getExpire();
        cache = new LinkedHashMap<>(config.getCapacity(),0.75f,true){
            @Override
            protected boolean removeEldestEntry(java.util.Map.Entry<K,ExpiredCacheValue<V>> eldest) {
                return size()>config.getCapacity();
            }
        };
    }
    @Override
    public void put(K key,V value){
        writeLock.lock();
        ExpiredCacheValue<V> expiredCacheValue=new ExpiredCacheValue<>(System.currentTimeMillis(),value);
        try{
            cache.put(key,expiredCacheValue);
        }finally {
            writeLock.unlock();
        }
    }
    @Override
    public V get(K key){
        readLock.lock();
        try{
            if(!cache.containsKey(key)){
                return null;
            }
            ExpiredCacheValue<V> expiredCacheValue= cache.get(key);
            if(System.currentTimeMillis()-expiredCacheValue.getLastTime()>expire){
                remove(key);
                return null;
            }else {
                expiredCacheValue.setLastTime(System.currentTimeMillis());
                return expiredCacheValue.getVal();
            }
        }finally {
            readLock.unlock();
        }
    }
    @Override
    public void clear(){
        writeLock.lock();
        try{
            cache.clear();
        }finally {
            writeLock.unlock();
        }
    }
    @Override
    public void remove(K key){
        writeLock.lock();
        try{
            cache.remove(key);
        }finally {
            writeLock.unlock();
        }
    }
    @Override
    public Long size(){
        readLock.lock();
        try{
            return (long) cache.size();
        }finally {
            readLock.unlock();
        }
    }
    @Override
    public String toString() {
        readLock.lock();
        try {
            return cache.toString();
        } finally {
            readLock.unlock();
        }
    }
    @Override
    public boolean containsKey(K key) {
        readLock.lock();
        try {
            return cache.containsKey(key);
        } finally {
            readLock.unlock();
        }
    }

}
