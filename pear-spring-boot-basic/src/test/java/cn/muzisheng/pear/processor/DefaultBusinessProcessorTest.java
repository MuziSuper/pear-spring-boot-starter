package cn.muzisheng.pear.processor;

import cn.muzisheng.pear.exception.IllegalException;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.SimpleTransactionStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DefaultBusinessProcessorTest {
    @Test
    void fillsTheCallerOwnedResponseOnSuccess() {
        DefaultBusinessProcessor processor = new DefaultBusinessProcessor(null);
        ProcessResponse<String> response = ProcessResponse.create();

        processor.execute(ProcessRequest.of("sample.success", "input"), response,
                (request, output) -> output.setData(request.getData() + "-output"));

        assertTrue(response.isSuccess());
        assertEquals("input-output", response.getData());
        assertEquals("sample.success", response.getMetadata().getOperation());
        assertTrue(response.getMetadata().getDurationMillis() >= 0);
    }

    @Test
    void wrapsBusinessExceptionsIntoTheCallerOwnedResponse() {
        DefaultBusinessProcessor processor = new DefaultBusinessProcessor(null);
        ProcessResponse<String> response = ProcessResponse.create();

        processor.execute(ProcessRequest.of("sample.failure", "input"), response,
                (request, output) -> { throw new IllegalException("invalid input"); });

        assertFalse(response.isSuccess());
        assertEquals("VALIDATION_ERROR", response.getError().getCode());
        assertEquals("invalid input", response.getError().getMessage());
    }

    @Test
    void rollsBackTransactionWhenTheCallbackFails() {
        PlatformTransactionManager transactionManager = mock(PlatformTransactionManager.class);
        TransactionStatus status = new SimpleTransactionStatus();
        when(transactionManager.getTransaction(any(TransactionDefinition.class))).thenReturn(status);
        DefaultBusinessProcessor processor = new DefaultBusinessProcessor(transactionManager);
        ProcessResponse<String> response = ProcessResponse.create();

        processor.executeInTransaction(ProcessRequest.of("sample.rollback", "input"), response,
                TransactionSpec.required(), (request, output) -> { throw new IllegalException("invalid input"); });

        assertFalse(response.isSuccess());
        verify(transactionManager).rollback(status);
    }
}
