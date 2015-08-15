package me.alexpanov.retries;

public final class Retries<Result> {

    private final Retryable<Result> retryable;
    private final Options<Result> options;

    public Retries(Retryable<Result> retryable) {
        this.retryable = retryable;
        this.options = new Options<Result>();
    }

    private Retries(Retryable<Result> retryable, Options<Result> options) {
        this.retryable = retryable;
        this.options = options;
    }

    public Retries<Result> stopOnMaxFailures(int maxRetries) {
        Options<Result> newOptions = options.maxRetries(maxRetries);
        return new Retries<Result>(retryable, newOptions);
    }

    public Result perform() throws FailedToComputeAValueException {
        RetryRuntime<Result> runtime = new RetryRuntime<Result>(retryable, options);
        while (runtime.hasWorkToDo()) {
            runtime = runtime.performUnitOfWork();
        }
        return runtime.workResult();
    }

    public Retries<Result> orElse(Result value) {
        Options<Result> newOptions = options.defaultResult(new ConcreteOptional<Result>(value));
        return new Retries<Result>(retryable, newOptions);
    }
}
