package cn.muzisheng.pear.properties;

import cn.muzisheng.pear.config.SiteConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties("pear.site")
public class SiteProperties {
    private String authPrefix;
    public void applyTo(SiteConfig config){
        if (authPrefix != null){
            config.setAuthPrefix(authPrefix);
        }
    }
}
