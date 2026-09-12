package cn.muzisheng.pear.processor;

import cn.muzisheng.pear.exception.BaseException;
import cn.muzisheng.pear.exception.ErrorCode;

/**
 * Mutable output envelope owned by the caller.
 * Business callbacks set only {@code data}; Processor owns {@code success}, {@code error} and timing metadata.
 */
public final class ProcessResponse<O> {
    private boolean success;
    private O data;
    private ProcessError error;
    private ProcessMetadata metadata;

    /** Creates an empty response that can be passed to a Processor call. */
    public static <O> ProcessResponse<O> create() { return new ProcessResponse<>(); }
    public boolean isSuccess() { return success; }
    public O getData() { return data; }
    public ProcessError getError() { return error; }
    public ProcessMetadata getMetadata() { return metadata; }
    /** Stores business output from a callback. This does not mark the execution successful by itself. */
    public void setData(O data) { this.data = data; }

    void begin(ProcessRequest<?> request) { this.metadata = new ProcessMetadata(request.getOperation(), request.getTraceId()); }
    void succeed() { this.success = true; this.error = null; finish(); }
    void fail(BaseException exception) { fail(exception.getErrorCode(), exception.getMessage(), exception.getDetails()); }
    void fail(ErrorCode code, String message) { fail(code, message, null); }
    private void fail(ErrorCode code, String message, java.util.Map<String, Object> details) {
        this.success = false;
        this.error = ProcessError.of(code, message, details);
        finish();
    }
    private void finish() { if (metadata != null) { metadata.finish(); } }
}
