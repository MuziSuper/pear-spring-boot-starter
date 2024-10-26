package cn.muzisheng.pear.configure;

import cn.muzisheng.pear.service.LogService;
import cn.muzisheng.pear.service.UserService;
import cn.muzisheng.pear.service.impl.LogServiceImpl;
import cn.muzisheng.pear.service.impl.UserServiceImpl;
import org.springframework.context.annotation.Bean;

public class ServiceConfiguration {
    @Bean
    public LogService logService(){
        return new LogServiceImpl();
    }
    @Bean
    public UserService userService(){
        return new UserServiceImpl();
    }

}
