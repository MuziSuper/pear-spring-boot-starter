package cn.muzisheng.pear.properties;

import cn.muzisheng.pear.constant.Constant;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(CacheProperties.class)
@EnableConfigurationProperties(CacheProperties.class)
public class CacheAutoConfigure {
    @Bean("cacheSize")
    @ConditionalOnMissingBean
    public Long cacheProperties(CacheProperties cacheProperties) {
        if(cacheProperties.getSize()==0L){
            cacheProperties.setSize(Constant.CACHE_EXPIRED);
        }
        return cacheProperties.getSize();
    }

}
