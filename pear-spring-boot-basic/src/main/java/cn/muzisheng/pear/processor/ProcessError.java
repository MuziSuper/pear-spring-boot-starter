package cn.muzisheng.pear.processor;

import cn.muzisheng.pear.exception.ErrorCode;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ProcessError {
    private final String code;
    private final String message;
    private final Map<String, Object> details;

    private ProcessError(String code, String message, Map<String, Object> details) {
        this.code = code;
        this.message = message;
        this.details = details == null ? Collections.emptyMap() : Collections.unmodifiableMap(new LinkedHashMap<>(details));
    }

    public static ProcessError of(ErrorCode errorCode, String message, Map<String, Object> details) {
        ErrorCode resolved = errorCode == null ? ErrorCode.SYSTEM_ERROR : errorCode;
        return new ProcessError(resolved.getCode(), message, details);
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
    public Map<String, Object> getDetails() { return details; }
}
