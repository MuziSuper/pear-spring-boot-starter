package cn.muzisheng.pear.config;

import cn.muzisheng.pear.Constant;
import jakarta.annotation.PostConstruct;
import lombok.Data;

@Data
public class SiteConfig {
    private String authPrefix= Constant.AUTH_PREFIX;
    @PostConstruct
    public void init() {
        if(authPrefix==null||authPrefix.isEmpty()){
            authPrefix=Constant.AUTH_PREFIX;
        }
    }
    public static LogConfig.Builder builder(){
        return new LogConfig.Builder();
    }

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
