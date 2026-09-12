package cn.muzisheng.pear;

import cn.muzisheng.pear.config.CacheConfig;
import cn.muzisheng.pear.exception.CacheException;
import cn.muzisheng.pear.model.CacheStrategyEnum;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CacheContainerTest {

    @Test
    void duplicateCreateWithSameNameAndTypeReturnsTrueAndKeepsExistingCache() {
        CacheContainer container = newContainer();

        assertTrue(container.createCache("users", 0, TimeUnit.MILLISECONDS, 2, CacheStrategyEnum.LRU, String.class, String.class));
        container.put("users", "a", "A");

        assertTrue(container.createCache("users", 0, TimeUnit.MILLISECONDS, 2, CacheStrategyEnum.LRU, String.class, String.class));

        assertEquals("A", container.get("users", "a"));
        assertEquals(1, container.size("users"));
    }

    @Test
    void duplicateCreateWithSameNameButDifferentTypeThrows() {
        CacheContainer container = newContainer();

        assertTrue(container.createCache("users", 0, TimeUnit.MILLISECONDS, 2, CacheStrategyEnum.LRU, String.class, String.class));

        assertThrows(
                CacheException.class,
                () -> container.createCache("users", 0, TimeUnit.MILLISECONDS, 2, CacheStrategyEnum.LFU, Integer.class, String.class)
        );
    }

    private static CacheContainer newContainer() {
        CacheConfig cacheConfig = new CacheConfig.Builder()
                .capacity(2)
                .expire(0)
                .strategy(CacheStrategyEnum.LRU)
                .build();
        return new CacheContainer(new CacheFactory(cacheConfig, null), cacheConfig);
    }
}
