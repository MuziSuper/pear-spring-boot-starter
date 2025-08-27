package cn.muzisheng.pear.config;

import cn.muzisheng.pear.Constant;
import lombok.Data;

/**
 * Jpa配置类，允许开发人员自行注入JpaConfig实例，但只允许通过内部Builder类创建
 **/
@Data
public class JpaConfig {
    private boolean autoCreate = Constant.DB_AUTO_CREATE;
    public static class Builder {
        private final JpaConfig jpaConfig = new JpaConfig();
        public Builder autoCreate(boolean autoCreate){
            jpaConfig.autoCreate = autoCreate;
            return this;
        }
        public JpaConfig build(){
            return jpaConfig;
        }
    }
}
