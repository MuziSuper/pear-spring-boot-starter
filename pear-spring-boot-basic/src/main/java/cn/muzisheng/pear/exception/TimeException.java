package cn.muzisheng.pear.exception;
/**
 * 时间转换错误
 * code: 506
 **/
public class TimeException extends RuntimeException{
    public TimeException(String message) {
        super(message);
    }
    public TimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
