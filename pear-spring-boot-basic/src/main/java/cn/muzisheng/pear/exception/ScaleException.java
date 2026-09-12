package cn.muzisheng.pear.exception;
/**
 *  JWT加解密失败
 *  code: 501
 **/
public class ScaleException extends BaseException {
    public ScaleException(String message){
        super(ErrorCode.SYSTEM_ERROR, message);
    }
    public ScaleException(String message, Throwable cause){
        super(ErrorCode.SYSTEM_ERROR, message,cause);
    }
}
