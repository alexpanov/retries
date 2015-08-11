package me.alexpanov.retries;

public final class Retries<Result> {

    private final Retryable<Result> retryable;
    private final Options<Result> options;

    public Retries(Retryable<Result> retryable) {
        this.retryable = retryable;
        this.options = new Options<Result>();
    }

    public Retries<Result> stopOnMaxFailures(int maxRetries) {
        options.maxRetries(maxRetries);
        return this;
    }

    public Result perform() throws FailedToComputeAValueException {
        RetryRuntime<Result> runtime = new RetryRuntime<Result>(retryable, options);
        while (runtime.hasWorkToDo()) {
            runtime = runtime.performUnitOfWork();
        }
        return runtime.workResult();
    }

    public Retries<Result> orElse(Result value) {
        options.defaultResult(new ConcreteOptional<Result>(value));
        return this;
    }
}
