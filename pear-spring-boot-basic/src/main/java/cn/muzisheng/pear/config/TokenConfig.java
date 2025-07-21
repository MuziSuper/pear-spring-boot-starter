package cn.muzisheng.pear.config;

import cn.muzisheng.pear.Constant;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.Setter;

@Data

public class TokenConfig {
    private String salt= Constant.TOKEN_DEFAULT_SECRET_SALT;
    private String head=Constant.TOKEN_DEFAULT_SECRET_PREFIX;
    private Long expire=Constant.TOKEN_DEFAULT_EXPIRE_DAY;
    private String issue=Constant.TOKEN_DEFAULT_ISSUER;
    private String subject=Constant.TOKEN_DEFAULT_SUBJECT;
    @PostConstruct
    public void init(){
        if(salt==null||salt.isEmpty()){
            salt=Constant.TOKEN_DEFAULT_SECRET_SALT;
        }
        if(head==null||head.isEmpty()){
            head=Constant.TOKEN_DEFAULT_SECRET_PREFIX;
        }
        if(expire==null||expire<=0){
            expire=Constant.TOKEN_DEFAULT_EXPIRE_DAY;
        }
        if(issue==null||issue.isEmpty()){
            issue=Constant.TOKEN_DEFAULT_ISSUER;
        }
        if(subject==null||subject.isEmpty()){
            subject=Constant.TOKEN_DEFAULT_SUBJECT;
        }
    }
    public static Builder builder(){
        return new Builder();
    }

    public static class Builder{
        private final TokenConfig tokenConfig=new TokenConfig();
        public Builder head(String head){
            tokenConfig.head=head;
            return this;
        }
        public Builder expire(Long expire){
            tokenConfig.expire=expire;
            return this;
        }
        public Builder issue(String issue){
            tokenConfig.issue=issue;
            return this;
        }
        public Builder subject(String subject){
            tokenConfig.subject=subject;
            return this;
        }
        public Builder salt(String salt){
            tokenConfig.salt=salt;
            return this;
        }
        public TokenConfig build(){
            return tokenConfig;
        }
    }
}
