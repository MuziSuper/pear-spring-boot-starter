package cn.muzisheng.pear;
/**
 * 缓存接口，定义缓存实例的行为
 **/
public interface CacheTemplate<K,V> {

    <HK,HV> boolean isCompatible(Class<HK> keyClass, Class<HV> valueClass);

    V get(K key);

    void put(K key, V value);

    void remove(K key);

    long size();
    void clear();
    boolean containsKey(K key);
    String toString();
    Class<K> getKeyClass();
    Class<V> getValueClass();
}
