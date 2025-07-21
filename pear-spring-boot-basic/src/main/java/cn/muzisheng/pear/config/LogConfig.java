package cn.muzisheng.pear.config;

import cn.muzisheng.pear.Constant;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.Setter;
import org.springframework.boot.logging.LogLevel;
@Data
public class LogConfig {
    private String level= Constant.LOG_DEFAULT_LEVEL;
    private String stdoutPattern=Constant.LOG_DEFAULT_PATTERN;
    private String filePattern=Constant.LOG_DEFAULT_FILE_PATTERN;
    private String filePath=Constant.LOG_DEFAULT_FILE_PATH;
    private String logCatalogueAddress= Constant.LOG_DEFAULT_LOG_CATALOGUE_PATH;
    private String warnCatalogueAddress=Constant.LOG_DEFAULT_WARN_CATALOGUE_PATH;
    private String errorCatalogueAddress=Constant.LOG_DEFAULT_ERROR_CATALOGUE_PATH;
    @PostConstruct
    public void validate() {
        if(level==null||level.isEmpty()){
            level=Constant.LOG_DEFAULT_LEVEL;
        }else{
            try {
                LogLevel.valueOf(level);
            }catch (IllegalArgumentException e){
                throw new IllegalArgumentException("Invalid log level: " + level);
            }
        }
        if(filePattern==null||filePattern.isEmpty()){
            filePattern=Constant.LOG_DEFAULT_FILE_PATTERN;
        }
        if(filePath==null||filePath.isEmpty()){
            filePath=Constant.LOG_DEFAULT_FILE_PATH;
        }
        if(logCatalogueAddress==null||logCatalogueAddress.isEmpty()){
            logCatalogueAddress=Constant.LOG_DEFAULT_LOG_CATALOGUE_PATH;
        }
        if(warnCatalogueAddress==null||warnCatalogueAddress.isEmpty()){
            warnCatalogueAddress=Constant.LOG_DEFAULT_WARN_CATALOGUE_PATH;
        }
        if(errorCatalogueAddress==null||errorCatalogueAddress.isEmpty()){
            errorCatalogueAddress=Constant.LOG_DEFAULT_ERROR_CATALOGUE_PATH;
        }

    }
    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {
        private final LogConfig config = new LogConfig();
        public Builder level(String level){
            config.level = level;
            return this;
        }
        public Builder stdoutPattern(String stdoutPattern){
            config.stdoutPattern = stdoutPattern;
            return this;
        }
        public Builder filePattern(String filePattern){
            config.filePattern = filePattern;
            return this;
        }
        public Builder filePath(String filePath){
            config.filePath = filePath;
            return this;
        }
        public Builder logCatalogueAddress(String logCatalogueAddress){
            config.logCatalogueAddress = logCatalogueAddress;
            return this;
        }
        public Builder warnCatalogueAddress(String warnCatalogueAddress){
            config.warnCatalogueAddress = warnCatalogueAddress;
            return this;
        }
        public Builder errorCatalogueAddress(String errorCatalogueAddress){
            config.errorCatalogueAddress = errorCatalogueAddress;
            return this;
        }
        public LogConfig build() {
            return config;
        }
    }
}
