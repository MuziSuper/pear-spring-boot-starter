package cn.muzisheng.pear.exception;
/**
 * 常规异常
 * code: 500
 **/
public class GeneralException extends RuntimeException {
    public GeneralException() {
        super();
    }

    public GeneralException(String message) {
        super(message);
    }
}
