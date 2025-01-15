package cn.muzisheng.pear.configure;

import cn.muzisheng.pear.dao.ConfigDAO;
import cn.muzisheng.pear.service.ConfigService;
import cn.muzisheng.pear.service.LogService;
import cn.muzisheng.pear.service.UserService;
import cn.muzisheng.pear.service.impl.ConfigServiceImpl;
import cn.muzisheng.pear.service.impl.LogServiceImpl;
import cn.muzisheng.pear.service.impl.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class CoreAutoConfiguration {
    private final Environment environment;
    public CoreAutoConfiguration(Environment environment, ConfigDAO configDAO) {
        this.environment = environment;
        this.configDAO = configDAO;
    }
    @Bean
    public UserService userService() {
        return new UserServiceImpl();
    }
    @Bean
    public LogService logService() {
        return new LogServiceImpl();
    }

    @Bean
    public ConfigService configService() {
        return new ConfigServiceImpl(environment,configDAO);
    }
}
