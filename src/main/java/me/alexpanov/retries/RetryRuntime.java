package me.alexpanov.retries;

import java.util.Collection;
import java.util.Collections;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newLinkedList;

final class RetryRuntime<Result> {

    private final Optional<Result> defaultResult;
    private final Collection<FailureSubscriber<Result>> failureSubscribers;
    private final ContinueCriteria<Result> continueCriteria;
    private final long timeout;

    RetryRuntime() {
        this(Optional.<Result>absent(), Collections.<FailureSubscriber<Result>>emptyList(),
             new ContinueCriteria<Result>(), 0L);
    }

    RetryRuntime(Optional<Result> defaultResult, Collection<FailureSubscriber<Result>> failureSubscribers,
                 ContinueCriteria<Result> continueCriteria,
                 long timeout) {
        this.defaultResult = defaultResult;
        this.failureSubscribers = failureSubscribers;
        this.continueCriteria = continueCriteria;
        this.timeout = timeout;
    }

    Result perform(Retryable<Result> retryable) {
        return new ExecutionOfRetryable<Result>(retryable, defaultResult, failureSubscribers, continueCriteria, timeout)
                .perform();
    }

    RetryRuntime<Result> defaultResult(Optional<Result> defaultResult) {
        checkArgument(defaultResult.isPresent(), "Default result must be present");
        checkState(!this.defaultResult.isPresent(), "Cannot specify two default results");
        return new RetryRuntime<Result>(defaultResult, failureSubscribers, continueCriteria, timeout);
    }

    RetryRuntime<Result> maxRetries(int maxRetries) {
        return new RetryRuntime<Result>(defaultResult, failureSubscribers, continueCriteria.maxRetries(maxRetries),
                                        timeout);
    }

    RetryRuntime<Result> ignoreIfResult(Predicate<? super Result> ignoreRule) {
        return new RetryRuntime<Result>(defaultResult, failureSubscribers,
                                        continueCriteria.withContinueOnResultRule(ignoreRule), timeout);
    }

    RetryRuntime<Result> onEachFailure(FailureSubscriber<Result> failureSubscriber) {
        Collection<FailureSubscriber<Result>> failureSubscribers = newLinkedList(this.failureSubscribers);
        failureSubscribers.add(failureSubscriber);
        return new RetryRuntime<Result>(defaultResult, failureSubscribers, continueCriteria, timeout);
    }

    RetryRuntime<Result> waitAtLeast(long timeout) {
        checkArgument(timeout >= 0);
        return new RetryRuntime<Result>(defaultResult, failureSubscribers, continueCriteria, timeout);
    }
}
