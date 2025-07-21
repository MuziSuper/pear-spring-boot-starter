package cn.muzisheng.pear.exception;
/**
 *  JWT加解密失败
 *  code: 501
 **/
public class ScaleException extends RuntimeException {
    public ScaleException(String message){
        super(message);
    }
    public ScaleException(String message, Throwable cause){
        super(message,cause);
    }
}
