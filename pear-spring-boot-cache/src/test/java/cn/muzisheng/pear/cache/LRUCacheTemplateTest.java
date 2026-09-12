package cn.muzisheng.pear.cache;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

class LRUCacheTemplateTest {

    @Test
    void evictsLeastRecentlyUsedEntryWhenCapacityIsReached() {
        LRUCacheTemplate<String, String> cache = new LRUCacheTemplate<>(2, 0, TimeUnit.MILLISECONDS, String.class, String.class);

        cache.put("a", "A");
        cache.put("b", "B");
        cache.put("c", "C");

        assertNull(cache.get("a"));
        assertEquals("B", cache.get("b"));
        assertEquals("C", cache.get("c"));
        assertEquals(2, cache.size());
    }

    @Test
    void getRefreshesAccessOrder() {
        LRUCacheTemplate<String, String> cache = new LRUCacheTemplate<>(2, 0, TimeUnit.MILLISECONDS, String.class, String.class);

        cache.put("a", "A");
        cache.put("b", "B");
        assertEquals("A", cache.get("a"));
        cache.put("c", "C");

        assertEquals("A", cache.get("a"));
        assertNull(cache.get("b"));
        assertEquals("C", cache.get("c"));
    }

    @Test
    void expiredGetReturnsNullAndRemovesEntry() throws Exception {
        LRUCacheTemplate<String, String> cache = new LRUCacheTemplate<>(2, 20, TimeUnit.MILLISECONDS, String.class, String.class);

        cache.put("a", "A");
        Thread.sleep(40);

        assertNull(cache.get("a"));
        assertEquals(0, cache.size());
    }

    @Test
    void zeroCapacityDoesNotLimitSize() {
        LRUCacheTemplate<Integer, String> cache = new LRUCacheTemplate<>(0, 0, TimeUnit.MILLISECONDS, Integer.class, String.class);

        for (int i = 0; i < 20; i++) {
            cache.put(i, "v" + i);
        }

        assertEquals(20, cache.size());
        assertEquals("v0", cache.get(0));
        assertEquals("v19", cache.get(19));
    }

    @Test
    void concurrentPutGetAndRemoveDoesNotDeadlockOrThrow() {
        LRUCacheTemplate<Integer, Integer> cache = new LRUCacheTemplate<>(64, 0, TimeUnit.MILLISECONDS, Integer.class, Integer.class);

        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> assertDoesNotThrow(() -> runConcurrently(cache)));
    }

    private static void runConcurrently(LRUCacheTemplate<Integer, Integer> cache) throws Exception {
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
}
