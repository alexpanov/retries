package me.alexpanov.retries;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;

public final class Retries<Result> {

    private final Retryable<Result> retryable;
    private final RetryRuntime<Result> runtime;

    public Retries(Retryable<Result> retryable) {
        this(retryable, new RetryRuntime<Result>());
    }

    private Retries(Retryable<Result> retryable, RetryRuntime<Result> runtime) {
        checkNotNull(retryable);
        checkNotNull(runtime);

        this.retryable = retryable;
        this.runtime = runtime;
    }

    public Result perform() throws RetryException {
        return runtime.perform(retryable);
    }

    public Retries<Result> stopOnMaxFailures(int maxRetries) {
        return new Retries<Result>(retryable, runtime.maxRetries(maxRetries));
    }

    public Retries<Result> orElse(Result value) {
        checkNotNull(value);
        return new Retries<Result>(retryable, runtime.defaultResult(Optional.of(value)));
    }

    public Retries<Result> ignoreIfResult(Predicate<? super Result> matches) {
        checkNotNull(matches);
        return new Retries<Result>(retryable, runtime.ignoreIfResult(matches));
    }

    public Retries<Result> onEachFailureDo(FailureSubscriber failureSubscriber) {
        checkNotNull(failureSubscriber);
        return new Retries<Result>(retryable, runtime.onEachFailure(failureSubscriber));
    }

    public Retries<Result> waitAfterFailureAtLeast(int timeout, TimeUnit timeUnit) {
        checkNotNull(timeUnit);
        return new Retries<Result>(retryable, runtime.waitAtLeast(timeUnit.toMillis(timeout)));
    }
}
