package me.alexpanov.retries;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;

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

    public Retries<Result> orElse(Result value) {
        Options<Result> newOptions = options.defaultResult(Optional.of(value));
        return new Retries<Result>(retryable, newOptions);
    }

    public Result perform() throws FailedToComputeAValueException {
        RetryRuntime<Result> runtime = new RetryRuntime<Result>(retryable, options);
        while (runtime.hasWorkToDo()) {
            runtime = runtime.performUnitOfWork();
        }
        return runtime.workResult();
    }

    public Retries<Result> ignoreIfResult(Predicate<? super Result> matches) {
        return new Retries<Result>(retryable, options.ignoreIfResult(matches));
    }
}
