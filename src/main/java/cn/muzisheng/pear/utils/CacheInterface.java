package cn.muzisheng.pear.utils;

public interface CacheInterface<K,V> {
    V get(K key);

    void put(K key, V value);

    void remove(K key);

    int size();
    void clear();
    boolean containsKey(K key);
    String toString();
}
