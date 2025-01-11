//package cn.muzisheng.pear.properties;
//
//import cn.muzisheng.pear.constant.Constant;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@ConditionalOnClass(TokenProperties.class)
//@EnableConfigurationProperties(TokenProperties.class)
//public class TokenAutoConfigure {
//    @Bean("tokenProperties")
//    @ConditionalOnMissingBean
//    public TokenProperties tokenProperties(TokenProperties tokenProperties) {
//        if(tokenProperties.getTokenExpire() == 0L|| tokenProperties.getTokenSalt().isEmpty()){
//            tokenProperties.setTokenExpire(Constant.TOKEN_DEFAULT_EXPIRE_DAY);
//        }
//        if(tokenProperties.getTokenHead()==null ||tokenProperties.getTokenHead().isEmpty()){
//            tokenProperties.setTokenHead(Constant.TOKEN_DEFAULT_SECRET_PREFIX);
//        }
//        if(tokenProperties.getTokenSalt() == null||tokenProperties.getTokenSalt().isEmpty()){
//            tokenProperties.setTokenSalt(Constant.TOKEN_DEFAULT_SECRET_SALT);
//        }
//        return tokenProperties;
//    }
//}
