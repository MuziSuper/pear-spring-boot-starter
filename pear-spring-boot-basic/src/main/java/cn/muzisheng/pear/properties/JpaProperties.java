package cn.muzisheng.pear.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
@Data
@ConfigurationProperties(prefix = "pear.starter.db")
public class JpaProperties {
    /**
     * 是否自动建表
     */
    private boolean autoCreate = false;

}
