package cn.muzisheng.pear.processor;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;

/**
 * Immutable transaction settings for {@link BusinessProcessor#executeInTransaction}.
 * Use the named factories for common propagation modes and {@link #of(Propagation, Isolation)}
 * when an explicit isolation level is required.
 */
public final class TransactionSpec {
    private final Propagation propagation;
    private final Isolation isolation;
    private final boolean readOnly;
    private final Integer timeout;

    private TransactionSpec(Propagation propagation, Isolation isolation, boolean readOnly, Integer timeout) {
        this.propagation = propagation;
        this.isolation = isolation;
        this.readOnly = readOnly;
        this.timeout = timeout;
    }

    /** Joins an existing transaction or starts a new one. */
    public static TransactionSpec required() { return new TransactionSpec(Propagation.REQUIRED, Isolation.DEFAULT, false, null); }
    /** Always starts a new transaction and suspends an existing one. */
    public static TransactionSpec requiresNew() { return new TransactionSpec(Propagation.REQUIRES_NEW, Isolation.DEFAULT, false, null); }
    /** Declares a required transaction as read-only. */
    public static TransactionSpec readOnly() { return new TransactionSpec(Propagation.REQUIRED, Isolation.DEFAULT, true, null); }
    public static TransactionSpec of(Propagation propagation, Isolation isolation) { return new TransactionSpec(propagation, isolation, false, null); }
    /** Returns a copy with a timeout in seconds. */
    public TransactionSpec withTimeout(int seconds) { return new TransactionSpec(propagation, isolation, readOnly, seconds); }
    public Propagation getPropagation() { return propagation; }
    public Isolation getIsolation() { return isolation; }
    public boolean isReadOnly() { return readOnly; }
    public Integer getTimeout() { return timeout; }
}
