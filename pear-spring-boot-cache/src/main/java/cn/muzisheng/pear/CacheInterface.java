package cn.muzisheng.pear;

public interface CacheInterface<K,V> {
    V get(K key);

    void put(K key, V value);

    void remove(K key);

    Long size();
    void clear();
    boolean containsKey(K key);
    String toString();
}
