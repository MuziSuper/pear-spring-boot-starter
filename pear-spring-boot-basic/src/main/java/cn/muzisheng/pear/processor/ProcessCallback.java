package cn.muzisheng.pear.processor;

@FunctionalInterface
public interface ProcessCallback<I, O> {
    /**
     * Performs business work using the request envelope and writes the result to the response.
     * Throwing a {@code BaseException} preserves its error code; other exceptions become a system error.
     */
    void execute(ProcessRequest<I> request, ProcessResponse<O> response) throws Exception;
}
