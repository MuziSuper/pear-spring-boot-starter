package cn.muzisheng.pear.cache;

import cn.muzisheng.pear.CacheTemplate;
import cn.muzisheng.pear.config.CacheConfig;
import cn.muzisheng.pear.model.ExpiredCacheValue;

import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
/**
 * LRU缓存, 基于LinkedHashMap实现, 使用读写锁保证线程安全，超过最大容量后，最近最久未使用的数据会被清除
 **/
public class LRUCacheTemplate<K,V> implements CacheTemplate<K,V> {
    private final LinkedHashMap<K, ExpiredCacheValue<V>> cache;
    private final ReadWriteLock readWriteLock=new ReentrantReadWriteLock();
    private final Lock readLock=readWriteLock.readLock();
    private final Lock writeLock=readWriteLock.writeLock();
    private final long expire;
    private final TimeUnit timeUnit;
    private final Class<K> keyClass;
    private final Class<V> valueClass;
    public LRUCacheTemplate(int capacity, long expire, TimeUnit timeUnit,Class<K> keyClass, Class<V> valueClass) {
        this.expire = expire;
        this.timeUnit = timeUnit;
        this.keyClass = keyClass;
        this.valueClass = valueClass;
        if(capacity==0) cache=new LinkedHashMap<>();
        else cache = new LinkedHashMap<>(capacity,0.75f,true){
            @Override
            protected boolean removeEldestEntry(java.util.Map.Entry<K,ExpiredCacheValue<V>> eldest) {
                return size()>capacity;
            }
        };
    }

    public LRUCacheTemplate(CacheConfig config, Class<K> keyClass, Class<V> valueClass) {
         this(config.getCapacity(),config.getExpire(),config.getTimeUnit(),keyClass,valueClass);
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
            if(expiredCacheValue==null) return null;
            if(System.currentTimeMillis()-expiredCacheValue.getLastTime()<expire * timeUnit.toMillis(1)||expire==0L){
                expiredCacheValue.setLastTime(System.currentTimeMillis());
                return expiredCacheValue.getVal();
            }else {
                remove(key);
                return null;
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
    public long size(){
        readLock.lock();
        try{
            return cache.size();
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
    public Class<K> getKeyClass() {
        return this.keyClass;
    }

    @Override
    public Class<V> getValueClass() {
        return this.valueClass;
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
