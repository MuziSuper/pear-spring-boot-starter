package cn.muzisheng.pear.controller;

import cn.muzisheng.pear.constant.Constant;
import cn.muzisheng.pear.exception.AuthorizationException;
import cn.muzisheng.pear.exception.IllegalException;
import cn.muzisheng.pear.exception.ScaleException;
import cn.muzisheng.pear.exception.UserException;
import cn.muzisheng.pear.params.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
/**
 * 异常处理器
 **/
@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler(UserException.class)
    public ResponseEntity<ExceptionResponse> handleUserException(UserException e) {
        ExceptionResponse errorResponse = new ExceptionResponse(Constant.SCALE_EXCEPTION, e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(ScaleException.class)
    public ResponseEntity<ExceptionResponse> handleScaleException(ScaleException e) {
        ExceptionResponse errorResponse = new ExceptionResponse(Constant.USER_EXCEPTION, e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(IllegalException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalException(IllegalException e) {
        ExceptionResponse errorResponse = new ExceptionResponse(Constant.ILLEGAL_EXCEPTION, e.getMessage()==null?Constant.ILLEGAL_MESSAGE_EXCEPTION:e.getMessage(),System.currentTimeMillis());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ExceptionResponse> handleAuthorizationException(AuthorizationException e) {
        ExceptionResponse errorResponse = new ExceptionResponse(Constant.UNAPPROVED_EXCEPTION, e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
}
