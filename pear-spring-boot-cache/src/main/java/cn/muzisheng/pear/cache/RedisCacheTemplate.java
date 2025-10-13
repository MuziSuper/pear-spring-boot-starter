package cn.muzisheng.pear.cache;

import cn.muzisheng.pear.CacheTemplate;
import cn.muzisheng.pear.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class RedisCacheTemplate<K, V> implements CacheTemplate<K, V> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final static Logger LOG = LoggerFactory.getLogger(RedisCacheTemplate.class);
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock writeLock = lock.writeLock();
    private final Lock readLock = lock.readLock();
    private final String CACHE_PREFIX = Constant.REDIS_PREFIX;
    private final Class<K> keyClass;
    private final Class<V> valueClass;

    public RedisCacheTemplate(RedisTemplate<String, Object> redisTemplate, Class<K> keyClass, Class<V> valueClass) {
        this.redisTemplate = redisTemplate;
        this.keyClass = keyClass;
        this.valueClass = valueClass;
    }

    public String buildKey(K key) {
        return CACHE_PREFIX + ":" + key;
    }
    /**
     * 判断缓存的key和value的类型是否匹配
     * @param keyClass 缓存的key的类型
     * @param valueClass 缓存的value的类型
     * @return 如果匹配则返回true，否则返回false
     **/
    @Override
    public<HK,HV> boolean isCompatible(Class<HK> keyClass, Class<HV> valueClass){
        return this.keyClass.isAssignableFrom(keyClass)&&this.valueClass.isAssignableFrom(valueClass);
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
    @SuppressWarnings("unchecked")
    public V get(K key) {
        try {
            readLock.lock();
            Object value = redisTemplate.opsForValue().get(buildKey(key));
            try {
                return (V) value;
            } catch (ClassCastException e) {
                LOG.warn("Data type error in the cache system");
                return null;
            }
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void put(K key, V value) {
        try {
            writeLock.lock();
            redisTemplate.opsForValue().set(buildKey(key), value);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void remove(K key) {
        try {
            writeLock.lock();
            redisTemplate.delete(buildKey(key));
        } finally {
            writeLock.unlock();
        }

    }

    @Override
    public long size() {
        AtomicLong keys = new AtomicLong();
        redisTemplate.execute((RedisCallback<Object>) connection -> {
            var keyCommands = connection.keyCommands();
            var scanOpts = ScanOptions.scanOptions()
                    .match(CACHE_PREFIX + "*")
                    .count(500)
                    .build();
            try (Cursor<byte[]> cursor = keyCommands.scan(scanOpts)) {
                while (cursor.hasNext()) {
                    keys.getAndIncrement();
                    cursor.next();
                }
            } catch (Exception e) {
                LOG.warn("Error during Redis SCAN for prefix: " + CACHE_PREFIX, e);
            }
            return null;
        });
        return keys.get();
    }

    @Override
    public void clear() {
        // 收集所有要删除的 key
        Set<String> keys = new HashSet<>();
        redisTemplate.execute((RedisCallback<Object>) (connection) -> {
            // 使用新的 keyCommands().scan() API 而不是旧的 connection.scan()
            var keyCommands = connection.keyCommands();
            var scanOpts = ScanOptions.scanOptions()
                    .match(CACHE_PREFIX + "*")
                    .count(500)
                    .build();
            try (Cursor<byte[]> cursor = keyCommands.scan(scanOpts)) {
                while (cursor.hasNext()) {
                    keys.add(new String(cursor.next()));
                }
            } catch (Exception e) {
                LOG.warn("Error during Redis SCAN for prefix: " + CACHE_PREFIX, e);
            }
            return null;
        });


        if (keys.isEmpty()) {
            return;
        }
        // 2. 分批调用 del 删除（因为 del(byte[]...) 的参数可能有限制）
        List<String> keyList = new ArrayList<>(keys);
        final int BATCH_SIZE = 100;  // 每次删除 100 个 key 为例
        int total = keyList.size();
        for (int i = 0; i < total; i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, total);
            List<String> subList = keyList.subList(i, end);

            redisTemplate.execute((RedisCallback<Long>) connection -> {
                var keyCommands = connection.keyCommands();
                byte[][] bkeys = new byte[subList.size()][];
                for (int j = 0; j < subList.size(); j++) {
                    bkeys[j] = subList.get(j).getBytes();
                }
                return keyCommands.del(bkeys);
            });
        }
    }

    @Override
    public boolean containsKey(K key) {
        return false;
    }

}
