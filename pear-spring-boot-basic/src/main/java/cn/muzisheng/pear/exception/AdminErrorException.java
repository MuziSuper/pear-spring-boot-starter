package cn.muzisheng.pear.exception;
/**
 * Admin操作失败
 * code: 507
 **/
public class AdminErrorException extends RuntimeException{
    public AdminErrorException(String message) {
        super(message);
    }
    public AdminErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
