package cn.muzisheng.pear.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@ToString
public class ExpiredCacheValue<V> {
    private long lastTime;
    private V val;

}
