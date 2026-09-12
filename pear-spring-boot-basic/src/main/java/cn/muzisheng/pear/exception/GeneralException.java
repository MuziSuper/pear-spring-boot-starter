package cn.muzisheng.pear.exception;
/**
 * 常规异常
 * code: 500
 **/
public class GeneralException extends BaseException {
    public GeneralException() {
        super(ErrorCode.SYSTEM_ERROR);
    }

    public GeneralException(String message) {
        super(ErrorCode.SYSTEM_ERROR, message);
    }
    public GeneralException(String message, Throwable cause) {
        super(ErrorCode.SYSTEM_ERROR, message, cause);
    }
}
