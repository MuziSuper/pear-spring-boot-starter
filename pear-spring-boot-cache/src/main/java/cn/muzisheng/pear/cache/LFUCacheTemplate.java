package cn.muzisheng.pear.cache;

import cn.muzisheng.pear.CacheTemplate;
import cn.muzisheng.pear.config.CacheConfig;
import cn.muzisheng.pear.model.ExpiredCacheValue;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * LFU缓存，访问次数越少，越早被删除，使用三个数据结构以及读写锁实现
 **/
public class LFUCacheTemplate<K, V> implements CacheTemplate<K, V> {
    private final int capacity;
    private final Map<K, ExpiredCacheValue<V>> cache; // 存储键值对
    private final Map<K, Integer> frequencyMap; // 存储键的访问频率
    private final TreeMap<Integer, LinkedHashSet<K>> frequencyKeysMap; // 频率到键集合的映射
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();
    private final long expire;
    private final TimeUnit timeUnit;
    private final Class<K> keyClass;
    private final Class<V> valueClass;

    public LFUCacheTemplate(int capacity, long expire, TimeUnit timeUnit, Class<K> keyClass, Class<V> valueClass) {
        this.capacity = capacity;
        this.expire = expire;
        this.timeUnit = timeUnit;
        this.cache = new HashMap<>();
        this.frequencyMap = new HashMap<>();
        this.frequencyKeysMap = new TreeMap<>();
        this.keyClass = keyClass;
        this.valueClass = valueClass;
    }

    public LFUCacheTemplate(CacheConfig config, Class<K> keyClass, Class<V> valueClass) {
        this(config.getCapacity(), config.getExpire(), config.getTimeUnit(), keyClass, valueClass);
    }


    @Override
    public V get(K key) {
        writeLock.lock();
        try {
            ExpiredCacheValue<V> value = cache.get(key);
            if (value == null) {
                return null;
            }
            if (isExpired(value)) {
                removeInternal(key);
                return null;
            }
            value.setLastTime(System.currentTimeMillis());
            incrementFrequencyInternal(key);
            return value.getVal();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void put(K key, V value) {
        writeLock.lock();
        try {
            ExpiredCacheValue<V> expiredCacheValue = cache.get(key);
            // 键已存在且未过期时更新值、滑动过期时间并增加频率。
            if (expiredCacheValue != null && !isExpired(expiredCacheValue)) {
                expiredCacheValue.setVal(value);
                expiredCacheValue.setLastTime(System.currentTimeMillis());
                incrementFrequencyInternal(key);
                return;
            }
            if (expiredCacheValue != null) {
                removeInternal(key);
            }
            if (capacity > 0 && cache.size() >= capacity) {
                evictLeastFrequentInternal();
            }
            ExpiredCacheValue<V> newExpiredCacheValue = new ExpiredCacheValue<>(System.currentTimeMillis(), value);
            cache.put(key, newExpiredCacheValue);
            frequencyMap.put(key, 1);
            frequencyKeysMap.computeIfAbsent(1, k -> new LinkedHashSet<>()).add(key);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 判断缓存值是否过期，调用方无需持锁但通常已持有写锁。
     **/
    private boolean isExpired(ExpiredCacheValue<V> value) {
        if (value == null) {
            return true;
        }
        return expire != 0L && System.currentTimeMillis() - value.getLastTime() >= expire * timeUnit.toMillis(1);
    }

    /**
     * 更新键的访问次数。调用方必须已持有写锁。
     **/
    private void incrementFrequencyInternal(K key) {
        Integer oldFrequency = frequencyMap.get(key);
        if (oldFrequency == null) {
            oldFrequency = 0;
        }
        if (oldFrequency > 0) {
            LinkedHashSet<K> oldKeys = frequencyKeysMap.get(oldFrequency);
            if (oldKeys != null) {
                oldKeys.remove(key);
                if (oldKeys.isEmpty()) {
                    frequencyKeysMap.remove(oldFrequency);
                }
            }
        }
        int newFrequency = oldFrequency + 1;
        frequencyMap.put(key, newFrequency);
        frequencyKeysMap.computeIfAbsent(newFrequency, k -> new LinkedHashSet<>()).add(key);
    }

    /**
     * 淘汰访问频率最低 bucket 中最早进入的键。调用方必须已持有写锁。
     **/
    private void evictLeastFrequentInternal() {
        if (frequencyKeysMap.isEmpty()) {
            return;
        }
        Integer lowestFrequency = frequencyKeysMap.firstKey();
        LinkedHashSet<K> keysWithLowestFrequency = frequencyKeysMap.get(lowestFrequency);
        if (keysWithLowestFrequency == null || keysWithLowestFrequency.isEmpty()) {
            frequencyKeysMap.remove(lowestFrequency);
            return;
        }

        // 同频率下淘汰最早进入该频率 bucket 的 key。
        K keyToRemove = keysWithLowestFrequency.iterator().next();
        removeInternal(keyToRemove);
    }

    /**
     * 同步删除三份 LFU 结构。调用方必须已持有写锁。
     **/
    private void removeInternal(K key) {
        if (!cache.containsKey(key)) {
            return;
        }
        cache.remove(key);
        Integer frequency = frequencyMap.remove(key);
        if (frequency == null) {
            return;
        }
        LinkedHashSet<K> keys = frequencyKeysMap.get(frequency);
        if (keys != null) {
            keys.remove(key);
            if (keys.isEmpty()) {
                frequencyKeysMap.remove(frequency);
            }
        }
    }

    /**
     * 移除缓存。
     **/
    public void remove(K key) {
        writeLock.lock();
        try {
            removeInternal(key);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public long size() {
        readLock.lock();
        try {
            return cache.size();
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
        readLock.lock();
        try {
            return "LFUCache{" +
                    ", cache=" + cache +
                    ", frequencyMap=" + frequencyMap +
                    ", frequencyKeysMap=" + frequencyKeysMap +
                    '}';
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Class<K> getKeyClass() {
        return keyClass;
    }

    @Override
    public Class<V> getValueClass() {
        return valueClass;
    }
}
