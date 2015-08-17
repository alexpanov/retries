package me.alexpanov.retries;

import com.google.common.base.Optional;

final class PerformedWork<Result> {

    private final int numberOfTries;
    private final Optional<Result> lastResult;

    PerformedWork() {
        this(Optional.<Result>absent(), 0);
    }

    private PerformedWork(Optional<Result> lastResult, int numberOfTries) {
        this.lastResult = lastResult;
        this.numberOfTries = numberOfTries;
    }

    int numberOfTries() {
        return numberOfTries;
    }

    PerformedWork<Result> tryEndedIn(Optional<Result> result) {
        return new PerformedWork<Result>(result, numberOfTries + 1);
    }

    Optional<Result> lastResult() {
        return lastResult;
    }
}
