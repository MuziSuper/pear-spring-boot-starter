package cn.muzisheng.pear.properties;

import cn.muzisheng.pear.Constant;
import cn.muzisheng.pear.config.CacheConfig;
import cn.muzisheng.pear.exception.GeneralException;
import cn.muzisheng.pear.model.CacheStrategyEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

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
        if (strategy != null&& !strategy.isEmpty()&& CacheStrategyEnum.contains(strategy)){
            config.setStrategy(CacheStrategyEnum.strategyOf( strategy));
        }
        if (timeUnit != null&& !timeUnit.isEmpty()){
            try{
                String newTimeUnit = timeUnit;
                switch (newTimeUnit) {
                    case "ns" -> newTimeUnit = Constant.NANOSECONDS_TIME_UNIT;
                    case "µs" -> newTimeUnit = Constant.MICROSECONDS_TIME_UNIT;
                    case "us" -> newTimeUnit = Constant.MILLISECONDS_TIME_UNIT;
                    case "ms" -> newTimeUnit = Constant.SECONDS_TIME_UNIT;
                    case "s" -> newTimeUnit = Constant.MINUTES_TIME_UNIT;
                    case "m" -> newTimeUnit = Constant.HOURS_TIME_UNIT;
                    case "h" -> newTimeUnit = Constant.DAYS_TIME_UNIT;
                }
            config.setTimeUnit(TimeUnit.valueOf(newTimeUnit));
            }catch (IllegalArgumentException e){
                throw new GeneralException("Invalid time unit: " + timeUnit);
            }
        }

    }
}
