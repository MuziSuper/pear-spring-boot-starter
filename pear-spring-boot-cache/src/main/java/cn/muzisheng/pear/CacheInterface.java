package cn.muzisheng.pear;
/**
 * 缓存接口，定义缓存实例的行为
 **/
public interface CacheInterface<K,V> {

    V get(K key);

    void put(K key, V value);

    void remove(K key);

    Long size();
    void clear();
    boolean containsKey(K key);
    String toString();
}
