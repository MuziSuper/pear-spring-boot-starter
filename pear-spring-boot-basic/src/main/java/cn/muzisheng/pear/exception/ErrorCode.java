package cn.muzisheng.pear.exception;

/**
 * Stable application error codes exposed by Pear's public APIs.
 */
public enum ErrorCode {
    VALIDATION_ERROR("VALIDATION_ERROR", 400),
    BUSINESS_ERROR("BUSINESS_ERROR", 400),
    UNAUTHORIZED("UNAUTHORIZED", 401),
    FORBIDDEN("FORBIDDEN", 403),
    NOT_FOUND("NOT_FOUND", 404),
    CONFLICT("CONFLICT", 409),
    SYSTEM_ERROR("SYSTEM_ERROR", 500);

    private final String code;
    private final int httpStatus;

    ErrorCode(String code, int httpStatus) {
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return code;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
