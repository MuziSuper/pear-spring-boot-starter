package cn.muzisheng.pear.exception;


import org.springframework.stereotype.Component;
/**
 * 传参错误
 * code: 400
 **/
@Component
public class IllegalException extends RuntimeException{

    public IllegalException(){
        super();
    }
    public IllegalException(String message){
        super(message);
    }
}
