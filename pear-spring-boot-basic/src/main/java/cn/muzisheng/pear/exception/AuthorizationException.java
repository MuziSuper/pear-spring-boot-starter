package cn.muzisheng.pear.exception;
/**
 * 未登录认证
 * code: 401
 **/
public class AuthorizationException extends BaseException{
    public AuthorizationException(){ super(ErrorCode.UNAUTHORIZED); }
    public AuthorizationException(String message){
        super(ErrorCode.UNAUTHORIZED, message);
    }
    public AuthorizationException(String message, Throwable cause){
        super(ErrorCode.UNAUTHORIZED, message, cause);
    }
}
