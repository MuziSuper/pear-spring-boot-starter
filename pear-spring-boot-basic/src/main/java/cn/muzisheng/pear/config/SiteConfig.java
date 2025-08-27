package cn.muzisheng.pear.config;

import cn.muzisheng.pear.Constant;
import jakarta.annotation.PostConstruct;
import lombok.Data;
/**
 * Site配置类，允许开发人员自行注入SiteConfig实例，但只允许通过内部Builder类创建
 **/
@Data
public class SiteConfig {
    private String authPrefix= Constant.AUTH_PREFIX;
    @PostConstruct
    public void init() {
        if(authPrefix==null||authPrefix.isEmpty()){
            authPrefix=Constant.AUTH_PREFIX;
        }
    }
    private SiteConfig(){}
    public static class Builder {
        private final SiteConfig config = new SiteConfig();
        public Builder logConfig(String authPrefix) {
            config.authPrefix = authPrefix;
            return this;
        }
        public SiteConfig build() {
            return config;
        }
    }
}
