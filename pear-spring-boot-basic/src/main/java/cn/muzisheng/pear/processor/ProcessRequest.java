package cn.muzisheng.pear.processor;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Immutable input envelope for a Processor execution.
 * {@code operation} is used only for logs and diagnostics; {@code data} is the business input.
 */
public final class ProcessRequest<I> {
    private final String operation;
    private final String traceId;
    private final I data;
    private final Map<String, Object> attributes;

    private ProcessRequest(String operation, String traceId, I data, Map<String, Object> attributes) {
        this.operation = operation == null || operation.isBlank() ? "unnamed" : operation;
        this.traceId = traceId == null || traceId.isBlank() ? UUID.randomUUID().toString() : traceId;
        this.data = data;
        this.attributes = attributes == null ? new LinkedHashMap<>() : new LinkedHashMap<>(attributes);
    }

    /** Creates a request with an automatically generated trace id. */
    public static <I> ProcessRequest<I> of(String operation, I data) {
        return new ProcessRequest<>(operation, null, data, null);
    }

    /** Creates a request with a caller-provided trace id and optional non-sensitive attributes. */
    public static <I> ProcessRequest<I> of(String operation, String traceId, I data, Map<String, Object> attributes) {
        return new ProcessRequest<>(operation, traceId, data, attributes);
    }

    public String getOperation() { return operation; }
    public String getTraceId() { return traceId; }
    public I getData() { return data; }
    public Map<String, Object> getAttributes() { return Map.copyOf(attributes); }
}
