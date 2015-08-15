package me.alexpanov.retries;

import java.util.Collection;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import static com.google.common.collect.Lists.newLinkedList;

final class Options<Result> {

    private final int maxRetries;
    private final Optional<Result> defaultResult;
    private final Iterable<Predicate<? super Result>> skips;

    public Options() {
        this(2, Optional.<Result>absent(), Lists.<Predicate<? super Result>>newLinkedList());
    }

    private Options(int maxRetries, Optional<Result> defaultResult, Iterable<Predicate<? super Result>> skips) {
        this.maxRetries = maxRetries;
        this.defaultResult = defaultResult;
        this.skips = skips;
    }

    public Options<Result> maxRetries(int maxRetries) {
        return new Options<Result>(maxRetries, defaultResult, skips);
    }

    public boolean isSatisfiedBy(WorkHistory<Result> workHistory) {
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

    public Options<Result> defaultResult(Optional<Result> defaultResult) {
        if (this.defaultResult.isPresent()) {
            throw new IllegalStateException("Default value has already been set: " + defaultResult.get());
        }
        return new Options<Result>(maxRetries, defaultResult, skips);
    }

    public Optional<Result> defaultResult() {
        return defaultResult;
    }

    public Options<Result> ignoreIfResult(Predicate<? super Result> skip) {
        Collection<Predicate<? super Result>> predicates = newLinkedList(skips);
        predicates.add(skip);
        return new Options<Result>(maxRetries, defaultResult, predicates);
    }
}
