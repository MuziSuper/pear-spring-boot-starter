package cn.muzisheng.pear.config;

import cn.muzisheng.pear.Constant;
import cn.muzisheng.pear.entity.Config;
import cn.muzisheng.pear.interceptor.JWTInterceptor;
import cn.muzisheng.pear.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/**
 * 拦截器配置, 用于拦截除登陆与注册页面的所有请求
 **/
@Configuration
public class WebConfiger implements WebMvcConfigurer {
    private final JWTInterceptor jwtInterceptor;
    private final ConfigService configService;

    public WebConfiger(JWTInterceptor jwtInterceptor, ConfigService configService){
        this.jwtInterceptor = jwtInterceptor;
        this.configService = configService;
    }
    @Override
    public void addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(configService.getValue(Constant.KEY_SITE_SIGNIN_URL), configService.getValue(Constant.KEY_SITE_SIGNUP_URL));
    }
}
