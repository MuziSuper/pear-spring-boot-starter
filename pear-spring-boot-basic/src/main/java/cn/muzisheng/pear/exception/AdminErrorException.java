package cn.muzisheng.pear.exception;
/**
 * Admin数据库操作失败
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
