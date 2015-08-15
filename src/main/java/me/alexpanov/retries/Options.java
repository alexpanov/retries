package me.alexpanov.retries;

import java.util.Collection;
import java.util.Collections;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import static com.google.common.collect.Lists.newLinkedList;

final class Options<Result> {

    private final int maxRetries;
    private final Optional<Result> defaultResult;
    private final Iterable<Predicate<? super Result>> skips;
    private final Iterable<FailureSubscriber> failureSubscribers;

    Options() {
        this(2, Optional.<Result>absent(), Collections.<Predicate<? super Result>>emptyList(),
             Collections.<FailureSubscriber>emptyList());
    }

    private Options(int maxRetries,
                    Optional<Result> defaultResult,
                    Iterable<Predicate<? super Result>> skips,
                    Iterable<FailureSubscriber> failureSubscribers) {
        this.maxRetries = maxRetries;
        this.defaultResult = defaultResult;
        this.skips = skips;
        this.failureSubscribers = failureSubscribers;
    }

    Options<Result> maxRetries(int maxRetries) {
        return new Options<Result>(maxRetries, defaultResult, skips, failureSubscribers);
    }

    boolean isSatisfiedBy(WorkHistory<Result> workHistory) {
        Optional<Result> lastResult = workHistory.lastResult();
        if (lastResult.isPresent()) {
            return notMatchesAnySkip(lastResult.get());
        }
        return workHistory.numberOfTries() >= maxRetries;
    }

    private boolean notMatchesAnySkip(final Result result) {
        Optional<Predicate<? super Result>> firstPositiveSkip = FluentIterable.from(skips).firstMatch(toSkip(result));
        return !firstPositiveSkip.isPresent();
    }

    private Predicate<Predicate<? super Result>> toSkip(final Result result) {
        return new Predicate<Predicate<? super Result>>() {
            @Override
            public boolean apply(Predicate<? super Result> input) {
                return input.apply(result);
            }
        };
    }

    Options<Result> defaultResult(Optional<Result> defaultResult) {
        if (this.defaultResult.isPresent()) {
            throw new IllegalStateException("Default value has already been set: " + defaultResult.get());
        }
        return new Options<Result>(maxRetries, defaultResult, skips, failureSubscribers);
    }

    Optional<Result> defaultResult() {
        return defaultResult;
    }

    Options<Result> ignoreIfResult(Predicate<? super Result> skip) {
        Collection<Predicate<? super Result>> predicates = newLinkedList(skips);
        predicates.add(skip);
        return new Options<Result>(maxRetries, defaultResult, predicates, failureSubscribers);
    }

    Options<Result> onEachFailure(FailureSubscriber failureSubscriber) {
        Collection<FailureSubscriber> failureSubscribers = newLinkedList(this.failureSubscribers);
        failureSubscribers.add(failureSubscriber);
        return new Options<Result>(maxRetries, defaultResult, skips, failureSubscribers);
    }

    Collection<FailureSubscriber> failureSubscribers() {
        return newLinkedList(failureSubscribers);
    }
}
