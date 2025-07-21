package cn.muzisheng.pear.config;

import cn.muzisheng.pear.Constant;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.Setter;

@Data
public class UserConfig {
    private String passwordSalt;
    public static Builder builder(){
        return new Builder();
    }
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
