package cn.muzisheng.pear.properties;

import cn.muzisheng.pear.config.UserConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * pear.starter.user 配置项映射。
 **/
@Data
@ConfigurationProperties("pear.starter.user")
public class UserProperties {
    private String passwordSalt;
    private Integer bcryptStrength;
    private String passwordAlgorithm;

    /**
     * 将外部配置覆盖到 UserConfig。
     **/
    public void applyTo(UserConfig config){
        if(passwordSalt!=null){
            config.setPasswordSalt(passwordSalt);
        }
        if(bcryptStrength!=null){
            config.setBcryptStrength(bcryptStrength);
        }
        if(passwordAlgorithm!=null){
            config.setPasswordAlgorithm(passwordAlgorithm);
        }
    }
}
