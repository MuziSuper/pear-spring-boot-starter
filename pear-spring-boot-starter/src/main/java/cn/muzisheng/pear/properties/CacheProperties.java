package cn.muzisheng.pear.properties;

import cn.muzisheng.pear.constant.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties("app.cache")
public class CacheProperties {
    private long size;
}
