package cn.muzisheng.pear.processor;

import cn.muzisheng.pear.exception.BaseException;
import cn.muzisheng.pear.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * Default processor implementation. It logs operation boundaries and writes callback failures into
 * the caller-provided response. When a transaction is requested, an internal control signal makes
 * Spring perform a real rollback without leaking that signal to the caller.
 */
public class DefaultBusinessProcessor implements BusinessProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultBusinessProcessor.class);
    private final PlatformTransactionManager transactionManager;

    public DefaultBusinessProcessor(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    /** {@inheritDoc} */
    public <I, O> void execute(ProcessRequest<I> request, ProcessResponse<O> response, ProcessCallback<I, O> callback) {
        invoke(request, response, callback);
    }

    @Override
    /** {@inheritDoc} */
    public <I, O> void executeInTransaction(ProcessRequest<I> request, ProcessResponse<O> response,
                                             TransactionSpec spec, ProcessCallback<I, O> callback) {
        if (transactionManager == null) {
            response.begin(request);
            response.fail(ErrorCode.SYSTEM_ERROR, "No PlatformTransactionManager is available.");
            return;
        }
        TransactionTemplate template = new TransactionTemplate(transactionManager, definition(spec));
        try {
            template.executeWithoutResult(status -> {
                invoke(request, response, callback);
                if (!response.isSuccess()) {
                    throw ProcessRollbackSignal.INSTANCE;
                }
            });
        } catch (ProcessRollbackSignal ignored) {
            // The response already contains the business error; this signal only forces Spring rollback.
        }
    }

    private DefaultTransactionDefinition definition(TransactionSpec spec) {
        TransactionSpec resolved = spec == null ? TransactionSpec.required() : spec;
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        definition.setPropagationBehavior(resolved.getPropagation().value());
        definition.setIsolationLevel(resolved.getIsolation().value());
        definition.setReadOnly(resolved.isReadOnly());
        if (resolved.getTimeout() != null) {
            definition.setTimeout(resolved.getTimeout());
        }
        return definition;
    }

    private <I, O> void invoke(ProcessRequest<I> request, ProcessResponse<O> response, ProcessCallback<I, O> callback) {
        if (request == null || response == null || callback == null) {
            throw new IllegalArgumentException("request, response and callback are required");
        }
        response.begin(request);
        long started = System.nanoTime();
        LOG.debug("processor start: operation={}, traceId={}", request.getOperation(), request.getTraceId());
        try {
            callback.execute(request, response);
            response.succeed();
            LOG.info("processor success: operation={}, traceId={}, durationMs={}", request.getOperation(), request.getTraceId(), elapsedMillis(started));
        } catch (BaseException e) {
            response.fail(e);
            LOG.warn("processor business failure: operation={}, traceId={}, code={}", request.getOperation(), request.getTraceId(), e.getErrorCode().getCode());
        } catch (Exception e) {
            response.fail(ErrorCode.SYSTEM_ERROR, "Internal processing error.");
            LOG.error("processor failure: operation={}, traceId={}", request.getOperation(), request.getTraceId(), e);
        }
    }

    private long elapsedMillis(long started) { return (System.nanoTime() - started) / 1_000_000; }

    private static final class ProcessRollbackSignal extends RuntimeException {
        private static final ProcessRollbackSignal INSTANCE = new ProcessRollbackSignal();

        private ProcessRollbackSignal() {
            super(null, null, false, false);
        }
    }
}
