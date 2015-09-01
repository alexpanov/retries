package me.alexpanov.retries;

import com.google.common.base.Optional;

final class PerformedWork<Result> {

    private final RetriesCount retriesCount;
    private final StopCriteria<Result> stopCriteria;
    private final Optional<Result> lastResult;

    PerformedWork(RetriesCount retriesCount, StopCriteria<Result> stopCriteria) {
        this(stopCriteria, Optional.<Result>absent(), retriesCount);
    }

    private PerformedWork(StopCriteria<Result> stopCriteria, Optional<Result> lastResult, RetriesCount retriesCount) {
        this.stopCriteria = stopCriteria;
        this.lastResult = lastResult;
        this.retriesCount = retriesCount;
    }

    PerformedWork<Result> tryEndedIn(Optional<Result> result) {
        return new PerformedWork<Result>(stopCriteria, result, retriesCount.increment());
    }

    Optional<Result> lastResult() {
        return lastResult;
    }

    boolean isDone() {
        return retriesCount.isMaxReached() || !stopCriteria.shouldContinue(lastResult());
    }

    Optional<Result> workResult() {
        if (stopCriteria.shouldContinue(lastResult())) {
            return Optional.absent();
        }
        return lastResult();
    }
}
