package cn.muzisheng.pear.exception;
/**
 * 当前用户权限不足
 * code：403
 **/
public class ForbiddenException extends BaseException{
        public ForbiddenException(){ super(ErrorCode.FORBIDDEN); }
        public ForbiddenException(String message){
            super(ErrorCode.FORBIDDEN, message);
        }
        public ForbiddenException(String message, Throwable cause){
            super(ErrorCode.FORBIDDEN, message, cause);
        }

}
