package me.alexpanov.retries;

import java.util.Collection;
import java.util.Collections;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

import static com.google.common.collect.Lists.newLinkedList;

final class WorkHistory<Result> {

    private final Collection<Optional<Result>> results;

    WorkHistory() {
        this(Collections.<Optional<Result>>emptyList());
    }

    private WorkHistory(Collection<Optional<Result>> results) {
        this.results = results;
    }

    public int numberOfTries() {
        return results.size();
    }

    public WorkHistory<Result> tryEndedIn(Optional<Result> result) {
        Collection<Optional<Result>> newResults = newLinkedList(results);
        newResults.add(result);
        return new WorkHistory<Result>(newResults);
    }

    public Optional<Result> lastResult() {
        return Iterables.getLast(results, Optional.<Result>absent());
    }
}
