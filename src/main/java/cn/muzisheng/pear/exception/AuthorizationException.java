package cn.muzisheng.pear.exception;
/**
 * 未登录认证
 * code: 401
 **/
public class AuthorizationException extends RuntimeException{
    public AuthorizationException(){}
    public AuthorizationException(String message){
        super(message);
    }
    public AuthorizationException(String message, Throwable cause){
        super(message, cause);
    }
}
