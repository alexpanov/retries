package me.alexpanov.retries;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Allows specification of retry strategy to repeat calls to a function unless all of the conditions are met.
 * Null result are skipped and other skip critera can be specified through predicates.
 * If no acceptable value is returned a default value will be returned if specified.
 * Default number of retries is 2.
 *
 * @param <Result>
 */
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

    /**
     * Specify the maximum number of executions.
     */
    public Retries<Result> stopOnMaxFailures(int maxRetries) {
        return new Retries<Result>(retryable, runtime.maxRetries(maxRetries));
    }

    /**
     * Specify a default value to return if maximum number repetitions was reached
     * and no satisfactory value was computed.
     */
    public Retries<Result> orElse(Result value) {
        checkNotNull(value);
        return new Retries<Result>(retryable, runtime.defaultResult(Optional.of(value)));
    }

    /**
     * Specify a skip condition to discard values returned by the Retryable.
     */
    public Retries<Result> ignoreIfResult(Predicate<? super Result> matches) {
        checkNotNull(matches);
        return new Retries<Result>(retryable, runtime.ignoreIfResult(matches));
    }

    /**
     * Perform an arbitrary action after each failed repetition.
     */
    public Retries<Result> onEachFailureDo(FailureSubscriber failureSubscriber) {
        checkNotNull(failureSubscriber);
        return new Retries<Result>(retryable, runtime.onEachFailure(failureSubscriber));
    }

    /**
     * Specify a timeout to wait after each failed repetition.
     */
    public Retries<Result> waitAfterFailureAtLeast(int timeout, TimeUnit timeUnit) {
        checkNotNull(timeUnit);
        return new Retries<Result>(retryable, runtime.waitAtLeast(timeUnit.toMillis(timeout)));
    }

    /**
     * Carry out the retries.
     *
     * @throws RetryException if no satisfactory value was computed and no default value was provided.
     */
    public Result perform() throws RetryException {
        return runtime.perform(retryable);
    }
}
