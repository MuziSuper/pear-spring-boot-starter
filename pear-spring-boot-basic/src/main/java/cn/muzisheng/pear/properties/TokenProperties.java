package cn.muzisheng.pear.properties;

import cn.muzisheng.pear.config.TokenConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * pear.starter.token 配置项映射。
 **/
@Data
@ConfigurationProperties(prefix = "pear.starter.token")
public class TokenProperties {
    private String head;
    private Long expire;
    private String issue;
    private String subject;
    private String secret;

    /**
     * 将外部配置覆盖到 TokenConfig。
     **/
    public void applyTo(TokenConfig config){

        if (head != null){
            config.setHead(head);
        }
        if (expire != null){
            config.setExpire(expire);
        }
        if (issue != null){
            config.setIssue(issue);
        }
        if (subject != null){
            config.setSubject(subject);
        }
        if (secret != null){
            config.setSecret(secret);
        }
    }
}
