package cn.muzisheng.pear.properties;

import cn.muzisheng.pear.Constant;
import cn.muzisheng.pear.config.LogConfig;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.annotation.Configuration;
@Data
@ConfigurationProperties("pear.log")
public class LogProperties {
    private String level;
    private String stdoutPattern;
    private String filePattern;
    private String filePath;
    private String logCatalogueAddress;
    private String warnCatalogueAddress;
    private String errorCatalogueAddress;
    public void applyTo(LogConfig config){
        if (level != null){
            config.setLevel(level);
        }
        if (stdoutPattern != null){
            config.setStdoutPattern(stdoutPattern);
        }
        if (filePattern != null){
            config.setFilePattern(filePattern);
        }
        if (filePath != null){
            config.setFilePath(filePath);
        }
        if (logCatalogueAddress != null){
            config.setLogCatalogueAddress(logCatalogueAddress);
        }
        if (warnCatalogueAddress != null){
            config.setWarnCatalogueAddress(warnCatalogueAddress);
        }
        if (errorCatalogueAddress != null){
            config.setErrorCatalogueAddress(errorCatalogueAddress);
        }
    }

}
