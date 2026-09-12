package cn.muzisheng.pear.exception;
/**
 * Admin操作失败
 * code: 507
 **/
public class AdminErrorException extends BaseException{
    public AdminErrorException(String message) {
        super(ErrorCode.BUSINESS_ERROR, message);
    }
    public AdminErrorException(String message, Throwable cause) {
        super(ErrorCode.BUSINESS_ERROR, message, cause);
    }
}
