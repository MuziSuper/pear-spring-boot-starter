package cn.muzisheng.pear.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpiredCacheValue<V> {
    private LocalDateTime lastTime;
    private V val;
}
