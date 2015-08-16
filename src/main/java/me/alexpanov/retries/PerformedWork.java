package me.alexpanov.retries;

import java.util.Collection;
import java.util.Collections;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

import static com.google.common.collect.Lists.newLinkedList;

final class PerformedWork<Result> {

    private final Collection<Optional<Result>> results;

    PerformedWork() {
        this(Collections.<Optional<Result>>emptyList());
    }

    private PerformedWork(Collection<Optional<Result>> results) {
        this.results = results;
    }

    int numberOfTries() {
        return results.size();
    }

    PerformedWork<Result> tryEndedIn(Optional<Result> result) {
        Collection<Optional<Result>> newResults = newLinkedList(results);
        newResults.add(result);
        return new PerformedWork<Result>(newResults);
    }

    Optional<Result> lastResult() {
        return Iterables.getLast(results, Optional.<Result>absent());
    }
}
