package cn.muzisheng.pear.utils;

public class CacheStrategy<K,V> {
    private CacheInterface<K,V> cache;
   public void setCacheStrategy(CacheInterface<K,V> cache){
        this.cache = cache;
    }
    public CacheStrategy(CacheInterface<K,V> cache){
        this.cache = cache;
    }
    public V get(K key){
        return cache.get(key);
    }
    public void put(K key, V value){
        cache.put(key, value);
    }
    public void remove(K key){
        cache.remove(key);
    }
    public void clear(){
        cache.clear();
    }
    public int size(){
        return cache.size();
    }
    public boolean containsKey(K key){
        return cache.containsKey(key);
    }
    public String toString(){
        return cache.toString();
    }

    public static void main(String[] args) {
        CacheStrategy<Integer,Integer> cacheStrategy = new CacheStrategy<>(new LRUCacheUtil<>(3, 2000));
        cacheStrategy.put(1,1);
        cacheStrategy.put(2,2);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(cacheStrategy.get(1));
        System.out.println(cacheStrategy.get(2));
        cacheStrategy.put(3,3);
        cacheStrategy.put(4,4);
        System.out.println(cacheStrategy.size());
        System.out.println(cacheStrategy);
        cacheStrategy.setCacheStrategy(new LFUCacheUtil<>(3, 2000));
        cacheStrategy.put(5,5);
        cacheStrategy.put(6,6);
        cacheStrategy.put(9,9);
        cacheStrategy.put(7,7);
        cacheStrategy.put(7,8);
        cacheStrategy.put(5,3);
        System.out.println(cacheStrategy.size());
        System.out.println(cacheStrategy);

    }
}
