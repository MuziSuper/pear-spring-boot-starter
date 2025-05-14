package cn.muzisheng.pear.exception;

public class UserException extends RuntimeException{

    public UserException(String email,String message) {
        super("User email: "+email+", "+message);
    }

    public UserException(String email, String message, Throwable cause) {
        super("User email: "+email+", "+message, cause);
    }
}

