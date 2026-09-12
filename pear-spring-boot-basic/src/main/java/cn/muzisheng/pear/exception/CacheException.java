package cn.muzisheng.pear.exception;
/**
 * 未登录认证
 * code: 508
 **/
public class CacheException extends BaseException{
    public CacheException(){
        super(ErrorCode.SYSTEM_ERROR);
    }
    public CacheException(String message){
        super(ErrorCode.SYSTEM_ERROR, message);
    }
    public CacheException(String message, Throwable cause){
        super(ErrorCode.SYSTEM_ERROR, message, cause);
    }
}
