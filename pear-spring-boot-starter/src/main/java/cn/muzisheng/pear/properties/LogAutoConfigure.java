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
        if(logProperties.getLogCatalogueAddress() == null|| logProperties.getLogCatalogueAddress().isEmpty()){
            logProperties.setLogCatalogueAddress(Constant.LOG_DEFAULT_LOG_CATALOGUE_PATH);
        }
        if(logProperties.getWarnCatalogueAddress() == null|| logProperties.getWarnCatalogueAddress().isEmpty()){
            logProperties.setWarnCatalogueAddress(Constant.LOG_DEFAULT_WARN_CATALOGUE_PATH);
        }
        if(logProperties.getErrorCatalogueAddress() == null|| logProperties.getErrorCatalogueAddress().isEmpty()){
            logProperties.setErrorCatalogueAddress(Constant.LOG_DEFAULT_ERROR_CATALOGUE_PATH);
        }
        if (logProperties.getFilePath() == null|| logProperties.getFilePath().isEmpty()){
            logProperties.setFilePath(Constant.LOG_DEFAULT_FILE_PATH);
        }
        if (logProperties.getFilePattern() == null|| logProperties.getFilePattern().isEmpty()){
            logProperties.setFilePattern(Constant.LOG_DEFAULT_FILE_PATTERN);
        }
        if (logProperties.getStdoutPattern() == null|| logProperties.getStdoutPattern().isEmpty()){
            logProperties.setStdoutPattern(Constant.LOG_DEFAULT_PATTERN);
        }
        if(logProperties.getLevel() == null|| logProperties.getLevel().isEmpty()){
            logProperties.setLevel(Constant.LOG_DEFAULT_LEVEL);
        }
        return logProperties;
    }
}
