package cn.muzisheng.pear.exception;
/**
 * User服务层异常
 * code: 502
 **/
public class UserException extends BaseException{

    public UserException(String email,String message) {
        super(ErrorCode.BUSINESS_ERROR, "User email: "+email+", "+message);
    }

    public UserException(String email, String message, Throwable cause) {
        super(ErrorCode.BUSINESS_ERROR, "User email: "+email+", "+message, cause);
    }
}

