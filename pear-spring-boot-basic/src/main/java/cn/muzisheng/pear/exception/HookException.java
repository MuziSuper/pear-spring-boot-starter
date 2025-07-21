package cn.muzisheng.pear.exception;
/**
 * 钩子函数异常
 * code: 504
 **/
public class HookException extends RuntimeException{
    public HookException() {
    }

    public HookException(String message) {
        super(message);
    }

}
