package cn.muzisheng.pear.exception;
/**
 * sql语句错误
 * code：505
 **/
public class SqlStatementException extends BaseException {
    public SqlStatementException(String message) {
        super(ErrorCode.SYSTEM_ERROR, message);
    }
    public SqlStatementException(String message, Throwable cause) {
        super(ErrorCode.SYSTEM_ERROR, message, cause);
    }
}
