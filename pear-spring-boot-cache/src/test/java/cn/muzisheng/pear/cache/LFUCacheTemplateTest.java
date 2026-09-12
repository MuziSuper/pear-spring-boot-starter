package cn.muzisheng.pear.cache;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LFUCacheTemplateTest {

    @Test
    void evictsLowestFrequencyKeyWhenCapacityIsReached() {
        LFUCacheTemplate<String, String> cache = newCache(2, 0);

        cache.put("a", "A");
        cache.put("b", "B");
        assertEquals("A", cache.get("a"));
        cache.put("c", "C");

        assertEquals("A", cache.get("a"));
        assertNull(cache.get("b"));
        assertEquals("C", cache.get("c"));
        assertFrequencyStructuresConsistent(cache);
    }

    @Test
    void evictsOldestKeyInLowestFrequencyBucketWhenFrequenciesTie() {
        LFUCacheTemplate<String, String> cache = newCache(2, 0);

        cache.put("a", "A");
        cache.put("b", "B");
        cache.put("c", "C");

        assertNull(cache.get("a"));
        assertEquals("B", cache.get("b"));
        assertEquals("C", cache.get("c"));
        assertFrequencyStructuresConsistent(cache);
    }

    @Test
    void getIncreasesFrequency() {
        LFUCacheTemplate<String, String> cache = newCache(2, 0);

        cache.put("a", "A");
        cache.put("b", "B");
        cache.get("a");
        cache.get("a");
        cache.get("b");
        cache.put("c", "C");

        assertEquals("A", cache.get("a"));
        assertNull(cache.get("b"));
        assertEquals("C", cache.get("c"));
        assertFrequencyStructuresConsistent(cache);
    }

    @Test
    void putExistingUnexpiredKeyUpdatesValueAndIncreasesFrequency() {
        LFUCacheTemplate<String, String> cache = newCache(2, 0);

        cache.put("a", "A");
        cache.put("b", "B");
        cache.put("a", "A2");
        cache.put("c", "C");

        assertEquals("A2", cache.get("a"));
        assertNull(cache.get("b"));
        assertEquals("C", cache.get("c"));
        assertFrequencyStructuresConsistent(cache);
    }

    @Test
    void expiredGetRemovesKeyAndLaterPutInsertsNewKeyAtFrequencyOne() throws Exception {
        LFUCacheTemplate<String, String> cache = newCache(2, 20);

        cache.put("a", "A");
        assertEquals("A", cache.get("a"));
        Thread.sleep(40);

        assertNull(cache.get("a"));
        assertEquals(0, cache.size());
        cache.put("b", "B");

        assertEquals("B", cache.get("b"));
        assertFrequencyStructuresConsistent(cache);
    }

    @Test
    void removeMissingKeyDoesNotThrow() {
        LFUCacheTemplate<String, String> cache = newCache(2, 0);

        assertDoesNotThrow(() -> cache.remove("missing"));
        assertEquals(0, cache.size());
        assertFrequencyStructuresConsistent(cache);
    }

    @Test
    void zeroCapacityDoesNotLimitSize() {
        LFUCacheTemplate<Integer, String> cache = new LFUCacheTemplate<>(0, 0, TimeUnit.MILLISECONDS, Integer.class, String.class);

        for (int i = 0; i < 20; i++) {
            cache.put(i, "v" + i);
        }

        assertEquals(20, cache.size());
        assertEquals("v0", cache.get(0));
        assertEquals("v19", cache.get(19));
        assertFrequencyStructuresConsistent(cache);
    }

    @Test
    void concurrentPutGetAndRemoveDoesNotDeadlockOrThrow() {
        LFUCacheTemplate<Integer, Integer> cache = new LFUCacheTemplate<>(64, 0, TimeUnit.MILLISECONDS, Integer.class, Integer.class);

        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> assertDoesNotThrow(() -> runConcurrently(cache)));
        assertFrequencyStructuresConsistent(cache);
    }

    private static LFUCacheTemplate<String, String> newCache(int capacity, long expire) {
        return new LFUCacheTemplate<>(capacity, expire, TimeUnit.MILLISECONDS, String.class, String.class);
    }

    private static void runConcurrently(LFUCacheTemplate<Integer, Integer> cache) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(8);
        try {
            List<Callable<Void>> tasks = new ArrayList<>();
            for (int worker = 0; worker < 8; worker++) {
                final int offset = worker * 1_000;
                tasks.add(() -> {
                    for (int i = 0; i < 500; i++) {
                        int key = offset + (i % 100);
                        cache.put(key, i);
                        cache.get(key);
                        if (i % 3 == 0) {
                            cache.remove(key);
                        }
                    }
                    return null;
                });
            }
            for (Future<Void> future : executor.invokeAll(tasks)) {
                future.get();
            }
        } finally {
            executor.shutdownNow();
        }
    }

    @SuppressWarnings("unchecked")
    private static <K, V> void assertFrequencyStructuresConsistent(LFUCacheTemplate<K, V> cacheTemplate) {
        try {
            Map<K, ?> cache = (Map<K, ?>) readField(cacheTemplate, "cache");
            Map<K, Integer> frequencyMap = (Map<K, Integer>) readField(cacheTemplate, "frequencyMap");
            Map<Integer, LinkedHashSet<K>> frequencyKeysMap = (Map<Integer, LinkedHashSet<K>>) readField(cacheTemplate, "frequencyKeysMap");

            assertEquals(cache.keySet(), frequencyMap.keySet());
            for (Map.Entry<Integer, LinkedHashSet<K>> entry : frequencyKeysMap.entrySet()) {
                assertFalse(entry.getValue().isEmpty());
                for (K key : entry.getValue()) {
                    assertTrue(cache.containsKey(key));
                    assertEquals(entry.getKey(), frequencyMap.get(key));
                }
            }
            for (Map.Entry<K, Integer> entry : frequencyMap.entrySet()) {
                assertTrue(frequencyKeysMap.containsKey(entry.getValue()));
                assertTrue(frequencyKeysMap.get(entry.getValue()).contains(entry.getKey()));
            }
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    private static Object readField(Object target, String fieldName) throws ReflectiveOperationException {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }
}
