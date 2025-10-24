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
        if (System.currentTimeMillis() - value.getLastTime() < expire * timeUnit.toMillis(1) || expire == 0L) {
            try {
                try {
                    writeLock.lock();
                    incrementFrequency(key);
                } finally {
                    readLock.lock();
                    writeLock.unlock();
                }
                return value.getVal();
            } finally {
                readLock.unlock();
            }
        } else {
            remove(key);
            return null;
        }

    }

    @Override
    public void put(K key, V value) {
        ExpiredCacheValue<V> expiredCacheValue;
        readLock.lock();
        try {
            expiredCacheValue = cache.get(key);
        } finally {
            readLock.unlock();
        }
        // 键已存在则更新值并增加频率之后退出，若键已存在且已过期，则移除该键执行后续逻辑
        if (expiredCacheValue != null) {
            if (System.currentTimeMillis() - expiredCacheValue.getLastTime() < expire * timeUnit.toMillis(1) || expire == 0L) {
                expiredCacheValue.setVal(value);
                expiredCacheValue.setLastTime(System.currentTimeMillis());
                writeLock.lock();
                try {
                    try {
                        cache.put(key, expiredCacheValue);
                        incrementFrequency(key);
                        return;
                    } finally {
                        readLock.lock();
                        writeLock.unlock();
                    }
                } finally {
                    readLock.unlock();
                }
            }
            remove(key);
        }
        // 键不存在，则创建新的键值对
        ExpiredCacheValue<V> newExpiredCacheValue = new ExpiredCacheValue<>(System.currentTimeMillis(), value);
        newExpiredCacheValue.setVal(value);
        newExpiredCacheValue.setLastTime(System.currentTimeMillis());
        writeLock.lock();
        try {
            try {
                while (cache.size() > capacity && capacity != 0) {
                    removeLeastFrequent();
                }
                cache.put(key, newExpiredCacheValue);
                incrementFrequency(key);
            } finally {
                readLock.lock();
                writeLock.unlock();
            }
        } finally {
            readLock.unlock();
        }
    }

    /**
     * 更新键的访问次数与频率到元素映射的集合
     **/
    private void incrementFrequency(K key) {
        int frequency = frequencyMap.getOrDefault(key, 0) + 1;
        frequencyMap.put(key, frequency);
        // 从旧频率集合中移除
        if (frequencyKeysMap.containsKey(frequency)) {
            frequencyKeysMap.get(frequency).remove(key);
            if (frequencyKeysMap.get(frequency).isEmpty()) {
                frequencyKeysMap.remove(frequency);
            }
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
        return "LFUCache{" +
                ", cache=" + cache.toString() +
                ", frequencyMap=" + frequencyMap.toString() +
                ", frequencyKeysMap=" + frequencyKeysMap.toString() +
                '}';
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
