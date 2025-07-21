package cn.muzisheng.pear.config.auto;

import cn.muzisheng.pear.config.TokenConfig;
import cn.muzisheng.pear.properties.LogProperties;
import cn.muzisheng.pear.properties.TokenProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TokenProperties.class)
public class TokenAutoConfiguration {
    @Bean
    public TokenConfig defaultTokenProperties(TokenProperties properties){
        TokenConfig tokenConfig = new TokenConfig();
        properties.applyTo(tokenConfig);
        return tokenConfig;
    }
}
