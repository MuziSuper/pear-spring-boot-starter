package cn.muzisheng.pear.config.auto;

import cn.muzisheng.pear.config.TokenConfig;
import cn.muzisheng.pear.properties.TokenProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * Token条件化装配类
 **/
@Configuration
@EnableConfigurationProperties(TokenProperties.class)
public class TokenAutoConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(TokenAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(TokenConfig.class)
    public TokenConfig defaultTokenProperties(TokenProperties properties){
        LOG.info("TokenConfig默认注册完成");
        TokenConfig tokenConfig = new TokenConfig.Builder().build();
        properties.applyTo(tokenConfig);
        return tokenConfig;
    }
}
