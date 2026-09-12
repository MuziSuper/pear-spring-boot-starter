package cn.muzisheng.pear.exception;
/**
 * 钩子函数异常
 * code: 504
 **/
public class HookException extends BaseException{
    public HookException() {
        super(ErrorCode.SYSTEM_ERROR);
    }

    public HookException(String message) {
        super(ErrorCode.SYSTEM_ERROR, message);
    }

}
