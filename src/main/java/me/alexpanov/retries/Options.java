package me.alexpanov.retries;

import java.util.Collection;
import java.util.Collections;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;

import static com.google.common.collect.Lists.newLinkedList;

final class Options<Result> {

    private final Optional<Result> defaultResult;
    private final Iterable<FailureSubscriber> failureSubscribers;

    private final ContinueCriteria<Result> continueCriteria;

    Options() {
        this(Optional.<Result>absent(), Collections.<FailureSubscriber>emptyList(), new ContinueCriteria<Result>());
    }

    private Options(Optional<Result> defaultResult,
                    Iterable<FailureSubscriber> failureSubscribers,
                    ContinueCriteria<Result> continueCriteria) {
        this.defaultResult = defaultResult;
        this.failureSubscribers = failureSubscribers;
        this.continueCriteria = continueCriteria;
    }

    Options<Result> maxRetries(int maxRetries) {
        return new Options<Result>(defaultResult, failureSubscribers, continueCriteria.maxRetries(maxRetries));
    }

    boolean isSatisfiedBy(PerformedWork<Result> performedWork) {
        return !continueCriteria.shouldBeContinuedAfter(performedWork);
    }

    Options<Result> defaultResult(Optional<Result> defaultResult) {
        if (this.defaultResult.isPresent()) {
            throw new IllegalStateException("Default value has already been set: " + defaultResult.get());
        }
        return new Options<Result>(defaultResult, failureSubscribers, continueCriteria);
    }

    Optional<Result> defaultResult() {
        return defaultResult;
    }

    Options<Result> ignoreIfResult(Predicate<? super Result> skip) {
        return new Options<Result>(defaultResult, failureSubscribers, continueCriteria.withContinueOnResultRule(skip));
    }

    Options<Result> onEachFailure(FailureSubscriber failureSubscriber) {
        Collection<FailureSubscriber> failureSubscribers = newLinkedList(this.failureSubscribers);
        failureSubscribers.add(failureSubscriber);
        return new Options<Result>(defaultResult, failureSubscribers, continueCriteria);
    }

    Collection<FailureSubscriber> failureSubscribers() {
        return newLinkedList(failureSubscribers);
    }
}
