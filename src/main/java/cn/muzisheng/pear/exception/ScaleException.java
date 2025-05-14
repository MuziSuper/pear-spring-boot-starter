package cn.muzisheng.pear.exception;
/**
 *  Encapsulated decimal conversion exception
 **/
public class ScaleException extends RuntimeException {
    public ScaleException(String message){
        super(message);
    }
    public ScaleException(String message, Throwable cause){
        super(message,cause);
    }
}
