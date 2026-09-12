package cn.muzisheng.pear.processor;

/**
 * Optional service-flow executor supplied by Pear.
 * <p>
 * Callers create both envelopes and pass them to the processor. The callback returns no value and
 * writes its success data to {@link ProcessResponse#setData(Object)}. Callback exceptions are
 * converted into the supplied response instead of being returned from this API.
 */
public interface BusinessProcessor {
    /** Executes a callback without opening a transaction. */
    <I, O> void execute(ProcessRequest<I> request, ProcessResponse<O> response, ProcessCallback<I, O> callback);

    /**
     * Executes a callback inside a transaction configured at call time.
     * A callback failure is recorded in {@code response} and forces the transaction to roll back.
     */
    <I, O> void executeInTransaction(ProcessRequest<I> request, ProcessResponse<O> response,
                                     TransactionSpec transactionSpec, ProcessCallback<I, O> callback);
}
