package cn.muzisheng.pear.api;

import cn.muzisheng.pear.constant.Constant;
import cn.muzisheng.pear.exception.*;
import cn.muzisheng.pear.params.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * 异常处理器
 **/
@ControllerAdvice
public class ExceptionApi {
    @ExceptionHandler(UserException.class)
    public ResponseEntity<ExceptionResponse> handleUserException(UserException e) {
        ExceptionResponse errorResponse = new ExceptionResponse(Constant.SCALE_EXCEPTION, e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ScaleException.class)
    public ResponseEntity<ExceptionResponse> handleScaleException(ScaleException e) {
        ExceptionResponse errorResponse = new ExceptionResponse(Constant.USER_EXCEPTION, e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalException(IllegalException e) {
        ExceptionResponse errorResponse = new ExceptionResponse(Constant.ILLEGAL_EXCEPTION, e.getMessage() == null ? Constant.ILLEGAL_MESSAGE_EXCEPTION : e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ExceptionResponse> handleAuthorizationException(AuthorizationException e) {
        ExceptionResponse errorResponse = new ExceptionResponse(Constant.UNAPPROVED_EXCEPTION, e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(HookException.class)
    public ResponseEntity<ExceptionResponse> handleHookException(HookException e) {
        ExceptionResponse errorResponse = new ExceptionResponse(Constant.HOOK_EXCEPTION, e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.GATEWAY_TIMEOUT);
    }
    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ExceptionResponse> handleGeneralException(GeneralException e) {
        ExceptionResponse errorResponse = new ExceptionResponse(Constant.GENERAL_EXCEPTION, e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(SqlStatementException.class)
    public ResponseEntity<ExceptionResponse> handleSqlStatementException(SqlStatementException e) {
        ExceptionResponse errorResponse = new ExceptionResponse(Constant.SQL_EXCEPTION, e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.HTTP_VERSION_NOT_SUPPORTED);
    }
}
