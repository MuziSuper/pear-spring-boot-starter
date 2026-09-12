package cn.muzisheng.pear.properties;

import cn.muzisheng.pear.config.CacheConfig;
import cn.muzisheng.pear.exception.GeneralException;
import cn.muzisheng.pear.model.CacheStrategyEnum;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CachePropertiesTest {

    @Test
    void parsesCommonTimeUnitAliases() {
        CacheProperties properties = new CacheProperties();

        assertEquals(TimeUnit.NANOSECONDS, properties.parseTimeUnit("ns"));
        assertEquals(TimeUnit.MICROSECONDS, properties.parseTimeUnit("us"));
        assertEquals(TimeUnit.MICROSECONDS, properties.parseTimeUnit("µs"));
        assertEquals(TimeUnit.MILLISECONDS, properties.parseTimeUnit("ms"));
        assertEquals(TimeUnit.MILLISECONDS, properties.parseTimeUnit("MILLISECONDS"));
        assertEquals(TimeUnit.SECONDS, properties.parseTimeUnit("s"));
        assertEquals(TimeUnit.SECONDS, properties.parseTimeUnit("seconds"));
        assertEquals(TimeUnit.MINUTES, properties.parseTimeUnit("m"));
        assertEquals(TimeUnit.HOURS, properties.parseTimeUnit("h"));
        assertEquals(TimeUnit.DAYS, properties.parseTimeUnit("d"));
    }

    @Test
    void appliesCachePropertiesAndNormalizesStrategy() {
        CacheProperties properties = new CacheProperties();
        properties.setExpire(10);
        properties.setCapacity(20);
        properties.setStrategy("lfu");
        properties.setTimeUnit("ms");
        CacheConfig config = new CacheConfig.Builder().build();

        properties.applyTo(config);

        assertEquals(10, config.getExpire());
        assertEquals(20, config.getCapacity());
        assertEquals(CacheStrategyEnum.LFU, config.getStrategy());
        assertEquals(TimeUnit.MILLISECONDS, config.getTimeUnit());
    }

    @Test
    void rejectsInvalidTimeUnitAndStrategy() {
        CacheProperties properties = new CacheProperties();

        assertThrows(GeneralException.class, () -> properties.parseTimeUnit("fortnight"));

        properties.setStrategy("random");
        assertThrows(GeneralException.class, () -> properties.applyTo(new CacheConfig.Builder().build()));
    }
}
