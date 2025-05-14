package cn.muzisheng.pear.exception;

public class SqlStatementException extends RuntimeException {
    public SqlStatementException(String message) {
        super(message);
    }
    public SqlStatementException(String message, Throwable cause) {
        super(message, cause);
    }
}
