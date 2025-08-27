package cn.muzisheng.pear.cache;

import cn.muzisheng.pear.CacheInterface;
import cn.muzisheng.pear.config.CacheConfig;
import cn.muzisheng.pear.model.ExpiredCacheValue;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * LFU缓存，访问次数越少，越早被删除，使用三个数据结构以及读写锁实现
 **/
public class LFUCache<K, V> implements CacheInterface<K, V> {
    private final int capacity;
    private final Map<K, ExpiredCacheValue<V>> cache; // 存储键值对
    private final Map<K, Integer> frequencyMap; // 存储键的访问频率
    private final TreeMap<Integer, LinkedHashSet<K>> frequencyKeysMap; // 频率到键集合的映射
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();
    private final long expire;
    private final String cacheName;

    public LFUCache(int capacity, long expire,String cacheName) {
        this.capacity = capacity;
        this.expire = expire;
        this.cacheName = cacheName;
        this.cache = new HashMap<>();
        this.frequencyMap = new HashMap<>();
        this.frequencyKeysMap = new TreeMap<>();
    }
    public LFUCache(CacheConfig config){
        this(config.getCapacity(),config.getExpire(),config.getCacheName());
    }

    @Override
    public V get(K key) {
        ExpiredCacheValue<V> value;
        try {
            readLock.lock();
            if (!cache.containsKey(key)) {
                return null;
            }
            value = cache.get(key);
        } finally {
            readLock.unlock();
        }
        if (System.currentTimeMillis() - value.getLastTime() > expire) {
            remove(key);
            return null;
        } else {
            try {
                try {
                    writeLock.lock();
                    incrementFrequency(key);
                } finally {
                    readLock.lock();
                    writeLock.unlock();
                }
                return value.getVal();
            }finally {
                readLock.unlock();
            }
        }

    }

    @Override
    public void put(K key, V value) {
        writeLock.lock();
        try {
            if (capacity <= 0) {
                return;
            }

            ExpiredCacheValue<V> expiredCacheValue = cache.get(key);
            if (cache.containsKey(key) && System.currentTimeMillis() - expiredCacheValue.getLastTime() <= expire) {
                expiredCacheValue.setVal(value);
                // 键已存在，更新值并增加频率
                cache.put(key, expiredCacheValue);
                incrementFrequency(key);
                return;
            }

            if (cache.size() >= capacity) {
                // 移除使用频率最低的键
                removeLeastFrequent();
            }
            // 添加新键值对
            ExpiredCacheValue<V> newValue = new ExpiredCacheValue<>(System.currentTimeMillis(), value);
            cache.put(key, newValue);
            frequencyMap.put(key, 1);
            frequencyKeysMap.computeIfAbsent(1, k -> new LinkedHashSet<>()).add(key);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 更新键的访问次数与频率到元素映射的集合
     **/
    private void incrementFrequency(K key) {
        int frequency = frequencyMap.get(key);
        frequencyMap.put(key, frequency + 1);

        // 从旧频率集合中移除
        frequencyKeysMap.get(frequency).remove(key);
        if (frequencyKeysMap.get(frequency).isEmpty()) {
            frequencyKeysMap.remove(frequency);
        }

        // 添加到新频率集合
        frequencyKeysMap.computeIfAbsent(frequency + 1, k -> new LinkedHashSet<>()).add(key);
    }

    private void removeLeastFrequent() {
        Integer lowestFrequency = frequencyKeysMap.firstKey();
        LinkedHashSet<K> keysWithLowestFrequency = frequencyKeysMap.get(lowestFrequency);

        // 获取并移除第一个键（LRU策略处理相同频率的情况）
        K keyToRemove = keysWithLowestFrequency.iterator().next();
        keysWithLowestFrequency.remove(keyToRemove);
        if (keysWithLowestFrequency.isEmpty()) {
            frequencyKeysMap.remove(lowestFrequency);
        }

        cache.remove(keyToRemove);
        frequencyMap.remove(keyToRemove);
    }

    /**
     * 移除缓存
     **/
    public void remove(K key) {
        readLock.lock();
        try {
            if (!cache.containsKey(key)) {
                return;
            }
            readLock.unlock();
            writeLock.lock();
            try {
                int frequency = frequencyMap.get(key);
                frequencyMap.remove(key);
                frequencyKeysMap.get(frequency).remove(key);
                if (frequencyKeysMap.get(frequency).isEmpty()) {
                    frequencyKeysMap.remove(frequency);
                }

                cache.remove(key);
            } finally {
                readLock.lock();
                writeLock.unlock();
            }
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Long size() {
        readLock.lock();
        try {
            return (long) cache.size();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void clear() {
        writeLock.lock();
        try {
            cache.clear();
            frequencyMap.clear();
            frequencyKeysMap.clear();
        } finally {
            writeLock.unlock();
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

    @Override
    public String toString() {
        return "LFUCache{" +
                "cacheName="+cacheName+
                ", cache=" + cache.toString() +
                ", frequencyMap=" + frequencyMap.toString() +
                ", frequencyKeysMap=" + frequencyKeysMap.toString() +
                '}';
    }
}
