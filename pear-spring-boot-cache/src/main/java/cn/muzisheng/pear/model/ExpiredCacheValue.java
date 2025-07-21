package cn.muzisheng.pear.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class ExpiredCacheValue<V> {
    private long lastTime;
    private V val;

}
