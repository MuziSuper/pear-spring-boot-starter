package cn.muzisheng.pear.exception;
/**
 * 时间转换错误
 * code: 506
 **/
public class TimeException extends BaseException{
    public TimeException(String message) {
        super(ErrorCode.SYSTEM_ERROR, message);
    }
    public TimeException(String message, Throwable cause) {
        super(ErrorCode.SYSTEM_ERROR, message, cause);
    }
}
