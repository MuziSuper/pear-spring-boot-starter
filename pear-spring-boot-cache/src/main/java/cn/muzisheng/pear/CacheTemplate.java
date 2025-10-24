package cn.muzisheng.pear;

/**
 * 缓存接口，定义缓存实例的行为
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 */
public interface CacheTemplate<K,V> {

    /**
     * 根据键名获取缓存值
     *
     * @param key 键
     * @return 值
     */
    V get(K key);

    /**
     * 添加缓存
     *
     * @param key 键
     * @param value 值
     */
    void put(K key, V value);

    /**
     * 根据键删除缓存
     *
     * @param key 键
     */
    void remove(K key);

    /**
     * 获取缓存大小
     *
     * @return 缓存大小
     */
    long size();

    /**
     * 清空缓存
     */
    void clear();

    /**
     * 判断缓存中是否存在指定键
     *
     * @param key 键
     * @return 存在返回true，否则返回false
     */
    boolean containsKey(K key);

    /**
     * 缓存信息
     * @return 缓存信息
     */
    String toString();

    /**
     * 获取键Class对象
     *
     * @return 获取键Class对象
     */
    Class<K> getKeyClass();

    /**
     * 获取值Class对象
     *
     * @return 获取值Class对象
     */
    Class<V> getValueClass();
}
