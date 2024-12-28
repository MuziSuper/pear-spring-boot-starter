package cn.muzisheng.pear.properties;

import cn.muzisheng.pear.constant.Constant;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(LogAutoConfigure.class)
@EnableConfigurationProperties(LogProperties.class)
public class LogAutoConfigure {
    @Bean("logConfig")
    @ConditionalOnMissingBean
    public LogProperties logProperties() {
        return new LogProperties("INFO","%d{yyyy-MM-dd HH:mm:ss.SSS} %highlight(%-5level) %-17black(%thread) %-82green(%logger{70}-%line) %highlight(%msg){black} %highlight(%ex){red} \\n","%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n","log","log/log-day","log/warn-day","log/error-day");
    }
}
