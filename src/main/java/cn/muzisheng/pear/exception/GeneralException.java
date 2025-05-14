package cn.muzisheng.pear.exception;
/**
 * 常规异常
 **/
public class GeneralException extends RuntimeException {
    public GeneralException() {
        super();
    }

    public GeneralException(String message) {
        super(message);
    }
}
