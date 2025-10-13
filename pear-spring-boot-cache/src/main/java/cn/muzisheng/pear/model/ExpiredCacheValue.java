package cn.muzisheng.pear.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class ExpiredCacheValue<V> {
    /**
     * 最后一次访问的时间，单位为毫秒
     **/
    private long lastTime;
    /**
     * 缓存的值
     **/
    private V val;

}
