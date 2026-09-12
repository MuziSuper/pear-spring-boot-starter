package cn.muzisheng.pear.config;

import cn.muzisheng.pear.Constant;
import jakarta.annotation.PostConstruct;
import lombok.Data;

/**
 * Token 配置类，允许开发人员自行注入 TokenConfig 实例，也可通过内部 Builder 创建。
 **/
@Data
public class TokenConfig {
    private String head=Constant.TOKEN_DEFAULT_SECRET_PREFIX;
    private Long expire=Constant.TOKEN_DEFAULT_EXPIRE_DAY;
    private String issue=Constant.TOKEN_DEFAULT_ISSUER;
    private String subject=Constant.TOKEN_DEFAULT_SUBJECT;
    private String secret=Constant.TOKEN_DEFAULT_SALT;
    private TokenConfig(){}

    /**
     * 规范化 token 配置，缺省项回落到框架默认值。
     **/
    @PostConstruct
    public void init(){
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
        if(secret==null||secret.isEmpty()){
            secret=Constant.TOKEN_DEFAULT_SALT;
        }

    }

    /**
     * 创建 TokenConfig 构建器。
     **/
    public static Builder builder(){
        return new Builder();
    }

    public static class Builder{
        private final TokenConfig tokenConfig=new TokenConfig();
        /**
         * 设置 Authorization 头中的 token 前缀。
         **/
        public Builder head(String head){
            tokenConfig.head=head;
            return this;
        }
        /**
         * 设置 token 过期时间，单位为毫秒。
         **/
        public Builder expire(Long expire){
            tokenConfig.expire=expire;
            return this;
        }
        /**
         * 设置 token issuer。
         **/
        public Builder issue(String issue){
            tokenConfig.issue=issue;
            return this;
        }
        /**
         * 设置 token subject。
         **/
        public Builder subject(String subject){
            tokenConfig.subject=subject;
            return this;
        }
        /**
         * 设置 HS256 签名密钥，支持 Base64 编码值或原始字符串。
         **/
        public Builder secret(String secret){
            tokenConfig.secret=secret;
            return this;
        }

        /**
         * 构建 TokenConfig。
         **/
        public TokenConfig build(){
            return tokenConfig;
        }
    }
}
