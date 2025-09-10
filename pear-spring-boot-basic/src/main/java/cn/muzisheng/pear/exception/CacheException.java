package cn.muzisheng.pear.exception;
/**
 * 未登录认证
 * code: 508
 **/
public class CacheException extends RuntimeException{
    public CacheException(){
        super();
    }
    public CacheException(String message){
        super(message);
    }
    public CacheException(String message, Throwable cause){
        super(message, cause);
    }
}
