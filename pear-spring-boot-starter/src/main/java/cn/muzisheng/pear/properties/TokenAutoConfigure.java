package cn.muzisheng.pear.properties;

import cn.muzisheng.pear.constant.Constant;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(TokenProperties.class)
@EnableConfigurationProperties(TokenProperties.class)
public class TokenAutoConfigure {
    @Bean("tokenConfig")
    @ConditionalOnMissingBean
    public TokenProperties tokenProperties() {
        return new TokenProperties(Constant.TOKEN_DEFAULT_SECRET_SALT, Constant.TOKEN_DEFAULT_SECRET_PREFIX,Constant.TOKEN_DEFAULT_EXPIRE_DAY);
    }
}
