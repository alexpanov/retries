package me.alexpanov.retries;

import java.util.Collection;

import com.google.common.base.Optional;

final class ExecutionOfRetryable<Result> {

    private final Retryable<Result> retryable;
    private final Optional<Result> defaultResult;
    private final Collection<FailureSubscriber> failureSubscribers;
    private final ContinueCriteria<Result> continueCriteria;

    ExecutionOfRetryable(Retryable<Result> retryable,
                         Optional<Result> defaultResult,
                         Collection<FailureSubscriber> failureSubscribers,
                         ContinueCriteria<Result> continueCriteria) {

        this.retryable = retryable;
        this.defaultResult = defaultResult;
        this.failureSubscribers = failureSubscribers;
        this.continueCriteria = continueCriteria;
    }

    public Result perform() {
        PerformedWork<Result> performedWork = new PerformedWork<Result>();
        while (hasWorkToDo(performedWork)) {
            Optional<Result> currentResult = performUnitOfWork();
            performedWork = performedWork.tryEndedIn(currentResult);
        }
        return workResult(performedWork);
    }

    private boolean hasWorkToDo(PerformedWork<Result> performedWork) {
        return continueCriteria.shouldBeContinuedAfter(performedWork);
    }

    private Optional<Result> performUnitOfWork() {
        try {
            return Optional.of(retryable.tryOnce());
        } catch (Exception e) {
            notifyOfFailure();
            return Optional.absent();
        }
    }

    private void notifyOfFailure() {
        for (FailureSubscriber failureSubscriber : failureSubscribers) {
            failureSubscriber.handle();
        }
    }

    private Result workResult(PerformedWork<Result> performedWork) {
        Optional<Result> workResult = performedWork.lastResult().or(defaultResult);
        if (workResult.isPresent()) {
            return workResult.get();
        }
        throw new FailedToComputeAValueException();
    }
}
