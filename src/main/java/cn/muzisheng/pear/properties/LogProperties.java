package cn.muzisheng.pear.properties;

import cn.muzisheng.pear.constant.Constant;
import cn.muzisheng.pear.params.LogLevel;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@NoArgsConstructor
@Configuration
@AllArgsConstructor
@ConfigurationProperties("app.log")
public class LogProperties {
    public String level=Constant.LOG_DEFAULT_LEVEL;
    public String stdoutPattern=Constant.LOG_DEFAULT_PATTERN;
    public String filePattern=Constant.LOG_DEFAULT_FILE_PATTERN;
    public String filePath=Constant.LOG_DEFAULT_FILE_PATH;
    public String logCatalogueAddress= Constant.LOG_DEFAULT_LOG_CATALOGUE_PATH;
    public String warnCatalogueAddress=Constant.LOG_DEFAULT_WARN_CATALOGUE_PATH;
    public String errorCatalogueAddress=Constant.LOG_DEFAULT_ERROR_CATALOGUE_PATH;
    @PostConstruct
    public void validate() {
        if(level!=null){
            try {
                LogLevel.valueOf(level);
            }catch (IllegalArgumentException e){
                throw new IllegalArgumentException("Invalid log level: " + level);
            }
        }
    }

}
