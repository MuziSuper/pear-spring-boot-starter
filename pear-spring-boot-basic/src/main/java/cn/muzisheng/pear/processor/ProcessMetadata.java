package cn.muzisheng.pear.processor;

public final class ProcessMetadata {
    private final String operation;
    private final String traceId;
    private final long startedAt;
    private long durationMillis;

    ProcessMetadata(String operation, String traceId) {
        this.operation = operation;
        this.traceId = traceId;
        this.startedAt = System.currentTimeMillis();
    }

    void finish() { this.durationMillis = System.currentTimeMillis() - startedAt; }
    public String getOperation() { return operation; }
    public String getTraceId() { return traceId; }
    public long getStartedAt() { return startedAt; }
    public long getDurationMillis() { return durationMillis; }
}
