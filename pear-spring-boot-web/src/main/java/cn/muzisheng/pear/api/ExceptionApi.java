package cn.muzisheng.pear.api;

import cn.muzisheng.pear.Constant;
import cn.muzisheng.pear.exception.*;
import cn.muzisheng.pear.vo.common.ErrorVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

/**
 * 异常处理器
 **/
@ControllerAdvice
public class ExceptionApi {
    private static final Logger LOG = LoggerFactory.getLogger(ExceptionApi.class);

    /**
     * 统一构建异常响应，保持 status/message/time 结构。
     **/
    private ResponseEntity<ErrorVO> response(int status, String message, HttpStatus httpStatus) {
        ErrorVO errorResponse = new ErrorVO(status, message, LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    @ExceptionHandler(IllegalException.class)
    public ResponseEntity<ErrorVO> handleIllegalException(IllegalException e) {
        return response(Constant.ILLEGAL_EXCEPTION, e.getMessage() == null ? Constant.ILLEGAL_MESSAGE_EXCEPTION : e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            java.lang.IllegalArgumentException.class
    })
    public ResponseEntity<ErrorVO> handleBadRequest(Exception e) {
        LOG.warn("bad request: {}", e.getMessage());
        return response(Constant.ILLEGAL_EXCEPTION, Constant.ILLEGAL_MESSAGE_EXCEPTION, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ErrorVO> handleAuthorizationException(AuthorizationException e) {
        return response(Constant.UNAPPROVED_EXCEPTION, e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorVO> handleForbiddenException(ForbiddenException e) {
        return response(Constant.FORBIDDEN_EXCEPTION, e.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorVO> handleNotFoundException(NotFoundException e) {
        return response(Constant.NOT_FOUND_EXCEPTION, e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ErrorVO> handleGeneralException(GeneralException e) {
        return response(Constant.GENERAL_EXCEPTION, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ScaleException.class)
    public ResponseEntity<ErrorVO> handleScaleException(ScaleException e) {
        return response(Constant.SCALE_EXCEPTION, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorVO> handleUserException(UserException e) {
        return response(Constant.USER_EXCEPTION, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(HookException.class)
    public ResponseEntity<ErrorVO> handleHookException(HookException e) {
        return response(Constant.HOOK_EXCEPTION, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(SqlStatementException.class)
    public ResponseEntity<ErrorVO> handleSqlStatementException(SqlStatementException e) {
        return response(Constant.SQL_EXCEPTION, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(TimeException.class)
    public ResponseEntity<ErrorVO> handleTimeException(TimeException e) {
        return response(Constant.TIME_EXCEPTION, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(AdminErrorException.class)
    public ResponseEntity<ErrorVO> handleTimeException(AdminErrorException e) {
        return response(Constant.ADMIN_EXCEPTION, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(CacheException.class)
    public ResponseEntity<ErrorVO> handleCacheExecption(CacheException e) {
        return response(Constant.CACHE_EXCEPTION, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /** Handles future BaseException subclasses without requiring a new Advice method. */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorVO> handleBaseException(BaseException e) {
        HttpStatus status = HttpStatus.valueOf(e.getErrorCode().getHttpStatus());
        String message = e.getMessage() == null ? e.getErrorCode().getCode() : e.getMessage();
        return new ResponseEntity<>(new ErrorVO(e.getErrorCode().getHttpStatus(), message, LocalDateTime.now()), status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorVO> handleException(Exception e) {
        LOG.error("unexpected server error", e);
        return response(Constant.GENERAL_EXCEPTION, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

