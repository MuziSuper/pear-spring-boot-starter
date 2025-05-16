package cn.muzisheng.pear.exception;
/**
 * 当前用户权限不足
 * code：403
 **/
public class ForbiddenException extends RuntimeException{
        public ForbiddenException(){}
        public ForbiddenException(String message){
            super(message);
        }
        public ForbiddenException(String message, Throwable cause){
            super(message, cause);
        }

}
