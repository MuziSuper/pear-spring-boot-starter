package cn.muzisheng.pear.properties;

import cn.muzisheng.pear.Constant;
import cn.muzisheng.pear.config.CacheConfig;
import cn.muzisheng.pear.exception.GeneralException;
import cn.muzisheng.pear.model.CacheStrategyEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * 缓存默认属性
 **/
@Data
@ConfigurationProperties("pear.starter.cache")
public class CacheProperties {

    private long expire;
    private int capacity;
    private String strategy;
    private String timeUnit;

    /**
     * 将properties属性应用到CacheConfig中
     **/
    public void applyTo(CacheConfig config){
        if (this.expire > 0){
            config.setExpire(this.expire);
        }
        if (this.capacity > 0){
            config.setCapacity(this.capacity);
        }
        if (strategy != null && !strategy.isBlank()){
            String normalizedStrategy = strategy.trim().toUpperCase(Locale.ROOT);
            if (!CacheStrategyEnum.contains(normalizedStrategy)) {
                throw new GeneralException("Invalid cache strategy: " + strategy);
            }
            config.setStrategy(CacheStrategyEnum.strategyOf(normalizedStrategy));
        }
        if (timeUnit != null&& !timeUnit.isBlank()){
            config.setTimeUnit(parseTimeUnit(timeUnit));
        }

    }

    /**
     * 将外部配置中的时间单位缩写或标准 TimeUnit 名称解析为 TimeUnit。
     **/
    public TimeUnit parseTimeUnit(String value) {
        if (value == null || value.isBlank()) {
            throw new GeneralException("Invalid time unit: " + value);
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        String timeUnitName = switch (normalized) {
            case "ns", "nano", "nanos", "nanosecond", "nanoseconds" -> Constant.NANOSECONDS_TIME_UNIT;
            case "µs", "us", "micro", "micros", "microsecond", "microseconds" -> Constant.MICROSECONDS_TIME_UNIT;
            case "ms", "milli", "millis", "millisecond", "milliseconds" -> Constant.MILLISECONDS_TIME_UNIT;
            case "s", "sec", "secs", "second", "seconds" -> Constant.SECONDS_TIME_UNIT;
            case "m", "min", "mins", "minute", "minutes" -> Constant.MINUTES_TIME_UNIT;
            case "h", "hr", "hrs", "hour", "hours" -> Constant.HOURS_TIME_UNIT;
            case "d", "day", "days" -> Constant.DAYS_TIME_UNIT;
            default -> value.trim().toUpperCase(Locale.ROOT);
        };
        try {
            return TimeUnit.valueOf(timeUnitName);
        } catch (IllegalArgumentException e) {
            throw new GeneralException("Invalid time unit: " + value);
        }
    }
}
