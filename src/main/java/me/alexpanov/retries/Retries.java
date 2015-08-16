package me.alexpanov.retries;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;

public final class Retries<Result> {

    private final Retryable<Result> retryable;
    private final RetryRuntime<Result> runtime;

    public Retries(Retryable<Result> retryable) {
        this(retryable, new RetryRuntime<Result>());
    }

    private Retries(Retryable<Result> retryable, RetryRuntime<Result> runtime) {
        this.retryable = retryable;
        this.runtime = runtime;
    }

    public Retries<Result> stopOnMaxFailures(int maxRetries) {
        return new Retries<Result>(retryable, runtime.maxRetries(maxRetries));
    }

    public Retries<Result> orElse(Result value) {
        return new Retries<Result>(retryable, runtime.defaultResult(Optional.of(value)));
    }

    public Result perform() throws FailedToComputeAValueException {
        return runtime.perform(retryable);
    }

    public Retries<Result> ignoreIfResult(Predicate<? super Result> matches) {
        return new Retries<Result>(retryable, runtime.ignoreIfResult(matches));
    }

    public Retries<Result> onEachFailure(FailureSubscriber failureSubscriber) {
        return new Retries<Result>(retryable, runtime.onEachFailure(failureSubscriber));
    }

    public Retries<Result> waitAfterFailuresAtLeast(long timeout) {
        return new Retries<Result>(retryable, runtime.waitAtLeast(timeout));
    }
}
