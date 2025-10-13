package cn.muzisheng.pear;

import cn.muzisheng.pear.config.CacheConfig;
import cn.muzisheng.pear.exception.CacheException;
import cn.muzisheng.pear.model.CacheStrategyEnum;
import jakarta.annotation.Nullable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class CacheContainer {
    private final CacheFactory cacheFactory;
    private final CacheConfig cacheConfig;
    private final ConcurrentHashMap<String, CacheTemplate<?, ?>> cacheTemplateMap = new ConcurrentHashMap<>();

    public CacheContainer(CacheFactory cacheFactory, CacheConfig cacheConfig) {
        this.cacheFactory = cacheFactory;
        this.cacheConfig = cacheConfig;
    }

    /**
     * 创建缓存放入缓存容器内,缓存名称作为主键存放。K为缓存的键类型,V为缓存的值类型。若对应缓存名称已存在则返回该缓存，但若想要创建的缓存键值对类型与已存在的缓存键值对类型不匹配则抛出异常。
     *
     * @param cacheName  缓存名称，主键值, 若传入字符串为空则随机生成一个十位随机字符串
     * @param expire     缓存过期时间, 0表示不过期
     * @param capacity   缓存容量
     * @param strategy   缓存策略
     * @param keyClass   缓存键类型
     * @param valueClass 缓存值类型
     * @throws CacheException 如果原本存在该缓存名
     **/
    @SuppressWarnings("unchecked")
    public <K, V> CacheTemplate<K, V> createCache(String cacheName, long expire, TimeUnit timeUnit, int capacity, CacheStrategyEnum strategy, Class<K> keyClass, Class<V> valueClass) {
        if (strategy == null || expire < 0 || capacity < 0) return null;
        if (StringUtil.isEmpty(cacheName)) cacheName = RandomStrUtil.getRandomStr(10);
        CacheTemplate<K, V> cacheTemplate = cacheFactory.createCacheTemplate(expire, timeUnit, capacity, strategy, keyClass, valueClass);
        CacheTemplate<?, ?> existing = cacheTemplateMap.putIfAbsent(cacheName, cacheTemplate);
        if (existing != null) {
            if (existing.isCompatible(keyClass, valueClass))
                throw new CacheException("The cache already exists, but there is an error in the type matching of the key-value pair.");
            return (CacheTemplate<K, V>) existing;
        }
        cacheTemplateMap.put(cacheName, cacheTemplate);
        return cacheTemplate;
    }

    /**
     * 创建缓存放入缓存容器内,缓存名称作为主键存放。K为缓存的键类型,V为缓存的值类型。该实例默认采用CacheConfig配置，若对应缓存名称已存在则返回该缓存，但若想要创建的缓存键值对类型与已存在的缓存键值对类型不匹配则抛出异常。
     *
     * @param cacheName  缓存名称，主键值, 若传入字符串为空则随机生成一个十位随机字符串
     * @param keyClass   缓存键类型
     * @param valueClass 缓存值类型
     * @throws CacheException 如果原本存在该缓存名
     **/
    public <K, V> CacheTemplate<K, V> createCache(String cacheName, Class<K> keyClass, Class<V> valueClass) {
        return createCache(cacheName, cacheConfig.getExpire(), cacheConfig.getTimeUnit(), cacheConfig.getCapacity(), cacheConfig.getStrategy(), keyClass, valueClass);
    }

    /**
     * 获取对应缓存中key映射的值，若缓存不存在则返回null。若你使用放入缓存内的不同值类型来接收返回值，则会抛出无法解决的ClassCastException异常，推荐使用get(String cacheName, K key, Class<V> valueClazz)方法。
     *
     * @param cacheName 缓存名称
     * @param key       缓存key
     * @return 缓存值
     * @throws CacheException 如果缓存键值对类型不匹配
     **/
    @SuppressWarnings("unchecked")
    public <K, V> V get(String cacheName, K key) {
        CacheTemplate<?, ?> cacheTemplate = cacheTemplateMap.get(cacheName);
        if (cacheTemplate == null) return null;
        Class<?> keyClass = cacheTemplate.getKeyClass();
        Class<?> valueClass = cacheTemplate.getValueClass();
        if (!keyClass.isInstance(key))
            throw new CacheException("The key type does not match the key type of the cache.");
        CacheTemplate<K, V> typedTemplate = (CacheTemplate<K, V>) cacheTemplate;
        V value = typedTemplate.get(key);
        if (value == null) return null;
        if (!valueClass.isInstance(value))
            throw new CacheException("Value type mismatch: expected " + valueClass.getName()
                    + ", but got " + value.getClass().getName());
        return value;
    }
    /**
     * 获取对应缓存中key映射的值，若缓存不存在则返回null,valueClass会将从缓存容器内取出的值做强制类型转换，若无法转换则抛出CacheException异常。
     *
     * @param cacheName 缓存名称
     * @param key       缓存key
     * @param valueClazz 缓存值类型
     * @return 缓存值
     * @throws CacheException 如果缓存键值对类型不匹配
     **/
    @SuppressWarnings("unchecked")
    public <K, V> V get(String cacheName, K key, Class<V> valueClazz) {
        CacheTemplate<?, ?> cacheTemplate = cacheTemplateMap.get(cacheName);
        if (cacheTemplate == null) return null;
        Class<?> keyClass = cacheTemplate.getKeyClass();
        Class<?> valueClass = cacheTemplate.getValueClass();
        if (!keyClass.isInstance(key))
            throw new CacheException("Key type mismatch: expected "+keyClass.getName()+", but got "+key.getClass().getName());
        CacheTemplate<K, V> typedTemplate = (CacheTemplate<K, V>) cacheTemplate;
        V value = typedTemplate.get(key);
        if (value == null) return null;
        try{
            return valueClazz.cast(value);
        }catch (ClassCastException e){
            throw new CacheException("Value type mismatch: expected "+valueClazz.getName()+", but got "+value.getClass().getName());
        }
    }


    /**
     * 向缓存中添加缓存键值对
     *
     * @param cacheName 缓存名称
     * @param key       缓存key
     * @param value     缓存值
     * @throws CacheException 如果缓存键值对类型不匹配
     **/
    @SuppressWarnings("unchecked")
    public <K, V> void put(String cacheName, K key, V value) {
        CacheTemplate<?, ?> cacheTemplate = cacheTemplateMap.get(cacheName);
        if (cacheTemplate == null) return;
        Class<?> keyClass = cacheTemplate.getKeyClass();
        Class<?> valueClass = cacheTemplate.getValueClass();
        if (!keyClass.isInstance(key) || !valueClass.isInstance(value))
            throw new CacheException("The specified key-value type does not match the type of the cached key-value.");
        CacheTemplate<K, V> typedTemplate = (CacheTemplate<K, V>) cacheTemplate;
        typedTemplate.put(key, value);
    }

    /**
     * 查询缓存中是否存在指定缓存键
     *
     * @param cacheName 缓存名称
     * @param key       缓存key
     * @throws CacheException 如果缓存键值对类型不匹配
     **/
    @SuppressWarnings("unchecked")
    public <K> boolean containsKey(String cacheName, K key) {
        CacheTemplate<?, ?> cacheTemplate = cacheTemplateMap.get(cacheName);
        if (cacheTemplate == null) return false;
        Class<?> keyClass = cacheTemplate.getKeyClass();
        if (!keyClass.isInstance(key))
            throw new CacheException("The key type does not match the key type of the cache.");
        CacheTemplate<K, ?> typedTemplate = (CacheTemplate<K, ?>) cacheTemplate;
        return typedTemplate.containsKey(key);
    }
    /**
     * 查询缓存中是否存在指定缓存键
     *
     * @param cacheName 缓存名称
     * @throws CacheException 如果缓存键值对类型不匹配
     **/
    public boolean contains(String cacheName) {
        return cacheTemplateMap.containsKey(cacheName);
    }
    /**
     * 查询缓存中键值对的数量
     *
     * @param cacheName 缓存名称
     * @throws CacheException 如果缓存键值对类型不匹配
     **/
    public long size(String cacheName) {
        CacheTemplate<?, ?> cacheTemplate = cacheTemplateMap.get(cacheName);
        if (cacheTemplate == null) return 0;
        return cacheTemplate.size();
    }
    /**
     * 删除缓存中指定缓存键的缓存键值对
     *
     * @param cacheName 缓存名称
     * @param key       缓存key
     * @throws CacheException 如果缓存键值对类型不匹配
     **/
    @SuppressWarnings("unchecked")
    public <K> void remove(String cacheName, K key) {
        CacheTemplate<?, ?> cacheTemplate = cacheTemplateMap.get(cacheName);
        if (cacheTemplate == null) return;
        Class<?> keyClass = cacheTemplate.getKeyClass();
        if (!keyClass.isInstance(key))
            throw new CacheException("The key type does not match the key type of the cache.");
        CacheTemplate<K, ?> typedTemplate = (CacheTemplate<K, ?>) cacheTemplate;
        typedTemplate.remove(key);
    }
    /**
     * 清空缓存
     *
     * @param cacheName 缓存名称
     **/
    public void clear(String cacheName) {
        CacheTemplate<?, ?> cacheTemplate = cacheTemplateMap.get(cacheName);
        if (cacheTemplate == null) return;
        cacheTemplate.clear();
    }

//    public static void main(String[] args) {
//        CacheConfig cacheConfig = new CacheConfig.Builder()
//                .expire(10)
//                .capacity(100)
//                .strategy(CacheStrategyEnum.LFU)
//                .build();
//        CacheFactory cacheFactory = new CacheFactory(null, cacheConfig);
//        CacheContainer cacheContainer = new CacheContainer(cacheFactory, cacheConfig);
//        cacheContainer.createCache("test", String.class, int[].class,null);
//        cacheContainer.put("test", "testKey", new int[]{1,2});
//       int[] value1 = cacheContainer.get("test", "testKey");
//        System.out.println(Arrays.toString(value1));
//        int[] value2 =cacheContainer.get("test", "testKey", int[].class);
//        System.out.println(Arrays.toString(value2));
//    }
}
