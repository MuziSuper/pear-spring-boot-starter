package cn.muzisheng.pear.exception;
/**
 * User服务层异常
 * code: 502
 **/
public class UserException extends RuntimeException{

    public UserException(String email,String message) {
        super("User email: "+email+", "+message);
    }

    public UserException(String email, String message, Throwable cause) {
        super("User email: "+email+", "+message, cause);
    }
}

