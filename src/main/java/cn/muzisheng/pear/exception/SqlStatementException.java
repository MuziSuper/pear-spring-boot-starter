package cn.muzisheng.pear.exception;
/**
 * sql语句错误
 * code：505
 **/
public class SqlStatementException extends RuntimeException {
    public SqlStatementException(String message) {
        super(message);
    }
    public SqlStatementException(String message, Throwable cause) {
        super(message, cause);
    }
}
