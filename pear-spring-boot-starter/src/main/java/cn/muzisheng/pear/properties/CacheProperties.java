package cn.muzisheng.pear.properties;

import cn.muzisheng.pear.constant.Constant;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Configuration
@ConfigurationProperties("app.cache")
public class CacheProperties {
    private long size=Constant.CACHE_EXPIRED;
}
