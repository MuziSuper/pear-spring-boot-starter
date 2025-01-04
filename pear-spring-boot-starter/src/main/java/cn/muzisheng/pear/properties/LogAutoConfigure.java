package cn.muzisheng.pear.properties;

import cn.muzisheng.pear.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnClass(LogProperties.class)
@EnableConfigurationProperties(LogProperties.class)
public class LogAutoConfigure {
    @Bean("logProperties")
    @ConditionalOnMissingBean
    public LogProperties logProperties(LogProperties logProperties) {
        logProperties.setLogCatalogueAddress(Constant.LOG_DEFAULT_LOG_CATALOGUE_PATH);
        logProperties.setWarnCatalogueAddress(Constant.LOG_DEFAULT_WARN_CATALOGUE_PATH);
        logProperties.setErrorCatalogueAddress(Constant.LOG_DEFAULT_ERROR_CATALOGUE_PATH);
        logProperties.setFilePath(Constant.LOG_DEFAULT_FILE_PATH);
        logProperties.setFilePattern(Constant.LOG_DEFAULT_FILE_PATTERN);
        logProperties.setLevel(Constant.LOG_DEFAULT_LEVEL);
        logProperties.setStdoutPattern(Constant.LOG_DEFAULT_PATTERN);
        return logProperties;
    }
}
