package me.alexpanov.retries;

final class RetryRuntime<Result> {

    private final Retryable<Result> retryable;
    private final Options<Result> options;

    RetryRuntime(Retryable<Result> retryable, Options<Result> options) {
        this.retryable = retryable;
        this.options = options;
    }

    public Result perform() {
        ExecutionOfRetryable<Result> execution = new ExecutionOfRetryable<Result>(retryable, options);
        while (execution.hasWorkToDo()) {
            execution = execution.performUnitOfWork();
        }
        return execution.workResult();
    }
}
