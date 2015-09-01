package me.alexpanov.retries;

import java.util.Collection;

import com.google.common.base.Optional;

final class ExecutionOfRetryable<Result> {

    private final Retryable<Result> retryable;
    private final Optional<Result> defaultResult;
    private final Collection<FailureSubscriber<Result>> failureSubscribers;
    private final StopCriteria<Result> stopCriteria;
    private final long timeout;

    ExecutionOfRetryable(Retryable<Result> retryable,
                         Optional<Result> defaultResult,
                         Collection<FailureSubscriber<Result>> failureSubscribers, StopCriteria<Result> stopCriteria,
                         long timeout) {

        this.retryable = retryable;
        this.defaultResult = defaultResult;
        this.failureSubscribers = failureSubscribers;
        this.stopCriteria = stopCriteria;
        this.timeout = timeout;
    }

    Result perform() {
        PerformedWork<Result> performedWork = new PerformedWork<Result>(stopCriteria);
        return computeResult(performedWork);
    }

    private Result computeResult(PerformedWork<Result> performedWork) {
        Optional<Result> result = obtainResultOfOneTry();
        PerformedWork<Result> workAfterCurrentTry = performedWork.tryEndedIn(result);
        if (workAfterCurrentTry.isDone()) {
            return workResult(workAfterCurrentTry);
        }
        handleFailure();
        return computeResult(workAfterCurrentTry);
    }

    private Result workResult(PerformedWork<Result> performedWork) {
        Optional<Result> workResult = performedWork.workResult().or(defaultResult);
        if (workResult.isPresent()) {
            return workResult.get();
        }
        throw new FailedToComputeAValueException();
    }




    private Optional<Result> obtainResultOfOneTry() {
        try {
            return Optional.fromNullable(retryable.tryOnce());
        } catch (Exception e) {
            return Optional.absent();
        }
    }

    private void handleFailure() {
        notifyOfFailure();
        waitForAtLeastSpecifiedTime();
    }

    private void notifyOfFailure() {
        RetryFailure<Result> retryFailure = new EmptyRetryFailure<Result>();
        for (FailureSubscriber<Result> failureSubscriber : failureSubscribers) {
            failureSubscriber.onFailure(retryFailure);
        }
    }

    private void waitForAtLeastSpecifiedTime() {
        new Wait(timeout).perform();
    }
}
