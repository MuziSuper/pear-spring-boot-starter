package cn.muzisheng.pear.config;

import cn.muzisheng.pear.Constant;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.Setter;
/**
 * 用户盐配置类，允许开发人员自行注入UserConfig实例，但只允许通过内部Builder类创建
 **/
@Data
public class UserConfig {
    private String passwordSalt;
    private UserConfig(){}
    @PostConstruct
    public void init(){
        if(passwordSalt==null){
            passwordSalt = Constant.APP_USER_PASSWORD_SALT;
        }
    }

    public static class Builder{
        private final UserConfig userConfig = new UserConfig();
        public Builder passwordSalt(String passwordSalt){
            userConfig.passwordSalt = passwordSalt;
            return this;
        }
        public UserConfig build(){
            return userConfig;
        }
    }
}
