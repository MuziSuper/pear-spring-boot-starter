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
    private long expire=Constant.CACHE_EXPIRED;
    private int capacity=Constant.CACHE_CAPACITY;
    @PostConstruct
    public void init() {
        if(expire<=0){
            expire=Constant.CACHE_EXPIRED;
        }
        if(capacity<=0){
            capacity=Constant.CACHE_CAPACITY;
        }
    }
}
