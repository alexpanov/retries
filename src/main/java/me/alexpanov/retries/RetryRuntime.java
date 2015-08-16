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
    private final Collection<FailureSubscriber> failureSubscribers;
    private final ContinueCriteria<Result> continueCriteria;

    RetryRuntime() {
        this(Optional.<Result>absent(), Collections.<FailureSubscriber>emptyList(), new ContinueCriteria<Result>());
    }

    RetryRuntime(Optional<Result> defaultResult,
                 Collection<FailureSubscriber> failureSubscribers,
                 ContinueCriteria<Result> continueCriteria) {
        this.defaultResult = defaultResult;
        this.failureSubscribers = failureSubscribers;
        this.continueCriteria = continueCriteria;
    }

    Result perform(Retryable<Result> retryable) {
        return new ExecutionOfRetryable<Result>(retryable, defaultResult, failureSubscribers,
                                                continueCriteria).perform();
    }

    RetryRuntime<Result> defaultResult(Optional<Result> defaultResult) {
        checkArgument(defaultResult.isPresent(), "Default result must be present");
        checkState(!this.defaultResult.isPresent(), "Cannot specify two default results");
        return new RetryRuntime<Result>(defaultResult, failureSubscribers, continueCriteria);
    }

    RetryRuntime<Result> maxRetries(int maxRetries) {
        return new RetryRuntime<Result>(defaultResult, failureSubscribers, continueCriteria.maxRetries(maxRetries));
    }

    RetryRuntime<Result> ignoreIfResult(Predicate<? super Result> ignoreRule) {
        return new RetryRuntime<Result>(defaultResult, failureSubscribers,
                                        continueCriteria.withContinueOnResultRule(ignoreRule));
    }

    RetryRuntime<Result> onEachFailure(FailureSubscriber failureSubscriber) {
        Collection<FailureSubscriber> failureSubscribers = newLinkedList(this.failureSubscribers);
        failureSubscribers.add(failureSubscriber);
        return new RetryRuntime<Result>(defaultResult, failureSubscribers, continueCriteria);
    }

    RetryRuntime<Result> waitAtLeast(long timeout) {
        return new RetryRuntime<Result>(defaultResult, failureSubscribers, continueCriteria);
    }
}
