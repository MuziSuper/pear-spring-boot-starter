package cn.muzisheng.pear.exception;


import org.springframework.stereotype.Component;
/**
 * 传参错误
 * code: 400
 **/
@Component
public class IllegalException extends BaseException{

    public IllegalException(){
        super(ErrorCode.VALIDATION_ERROR);
    }
    public IllegalException(String message){
        super(ErrorCode.VALIDATION_ERROR, message);
    }
}
