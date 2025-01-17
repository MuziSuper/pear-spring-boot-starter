package cn.muzisheng.pear.exception;


import org.springframework.stereotype.Component;

@Component
public class IllegalException extends RuntimeException{
    /**
     * 用户传入参数不正确，返回400状态码
     **/
    public IllegalException(){
        super();
    }
    public IllegalException(String message){
        super(message);
    }
}
