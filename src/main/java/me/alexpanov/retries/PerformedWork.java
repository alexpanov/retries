package me.alexpanov.retries;

import com.google.common.base.Optional;

final class PerformedWork<Result> {

    private final int numberOfTries;
    private final StopCriteria<Result> stopCriteria;
    private final Optional<Result> lastResult;

    public PerformedWork(StopCriteria<Result> stopCriteria) {
        this(stopCriteria, Optional.<Result>absent(), 0);
    }

    private PerformedWork(StopCriteria<Result> stopCriteria, Optional<Result> lastResult, int numberOfTries) {
        this.stopCriteria = stopCriteria;
        this.lastResult = lastResult;
        this.numberOfTries = numberOfTries;
    }

    int numberOfTries() {
        return numberOfTries;
    }

    PerformedWork<Result> tryEndedIn(Optional<Result> result) {
        return new PerformedWork<Result>(stopCriteria, result, numberOfTries + 1);
    }

    Optional<Result> lastResult() {
        return lastResult;
    }

    boolean isDone() {
        return !stopCriteria.shouldBeContinuedAfter(this);
    }

    Optional<Result> workResult() {
        if (stopCriteria.anyRuleMatches(this)) {
            return Optional.absent();
        }
        return lastResult();
    }
}
