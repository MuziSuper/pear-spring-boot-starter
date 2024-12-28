package cn.muzisheng.pear.properties;

import cn.muzisheng.pear.params.LogLevel;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties("app.log")
public class LogProperties {
    public String level;
    public String stdoutPattern;
    public String filePattern;
    public String filePath;
    public String logCatalogueAddress;
    public String warnCatalogueAddress;
    public String errorCatalogueAddress;
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
