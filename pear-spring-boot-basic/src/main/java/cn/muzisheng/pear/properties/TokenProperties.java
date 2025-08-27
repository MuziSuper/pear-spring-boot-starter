package cn.muzisheng.pear.properties;

import cn.muzisheng.pear.Constant;
import cn.muzisheng.pear.config.TokenConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
@Data
@ConfigurationProperties(prefix = "pear.starter.token")
public class TokenProperties {
    private String salt;
    private String head;
    private Long expire;
    private String issue;
    private String subject;
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
    }
}
