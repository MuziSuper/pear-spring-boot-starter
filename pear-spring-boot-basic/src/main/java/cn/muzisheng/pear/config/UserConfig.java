package cn.muzisheng.pear.config;

import cn.muzisheng.pear.Constant;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import cn.muzisheng.pear.exception.IllegalException;

/**
 * 用户密码配置类。passwordSalt 仅用于兼容旧 SHA-256 密码哈希。
 **/
@Data
public class UserConfig {
    public static final String PASSWORD_ALGORITHM_BCRYPT = "bcrypt";
    private String passwordSalt;
    private Integer bcryptStrength = 10;
    private String passwordAlgorithm = PASSWORD_ALGORITHM_BCRYPT;
    private UserConfig(){}

    /**
     * 规范化用户密码配置，并校验 BCrypt strength。
     **/
    @PostConstruct
    public void init(){
        if(passwordSalt==null){
            passwordSalt = Constant.APP_USER_PASSWORD_SALT;
        }
        if (bcryptStrength == null) {
            bcryptStrength = 10;
        }
        if (bcryptStrength < 4 || bcryptStrength > 31) {
            throw new IllegalException("bcryptStrength must be between 4 and 31.");
        }
        if (passwordAlgorithm == null || passwordAlgorithm.isBlank()) {
            passwordAlgorithm = PASSWORD_ALGORITHM_BCRYPT;
        }
        if (!PASSWORD_ALGORITHM_BCRYPT.equalsIgnoreCase(passwordAlgorithm)) {
            throw new IllegalException("Unsupported passwordAlgorithm: " + passwordAlgorithm);
        }
    }

    public static class Builder{
        private final UserConfig userConfig = new UserConfig();
        /**
         * 设置旧 SHA-256 密码盐，仅用于 legacy hash 校验。
         **/
        public Builder passwordSalt(String passwordSalt){
            userConfig.passwordSalt = passwordSalt;
            return this;
        }
        /**
         * 设置 BCrypt strength。
         **/
        public Builder bcryptStrength(Integer bcryptStrength){
            userConfig.bcryptStrength = bcryptStrength;
            return this;
        }
        /**
         * 设置密码算法标识，当前默认使用 bcrypt。
         **/
        public Builder passwordAlgorithm(String passwordAlgorithm){
            userConfig.passwordAlgorithm = passwordAlgorithm;
            return this;
        }
        public UserConfig build(){
            return userConfig;
        }
    }
}
