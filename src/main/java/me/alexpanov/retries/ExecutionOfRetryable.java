package me.alexpanov.retries;

import java.util.Collection;

import com.google.common.base.Optional;

final class ExecutionOfRetryable<Result> {

    private final Retryable<Result> retryable;
    private final Optional<Result> defaultResult;
    private final Collection<FailureSubscriber<Result>> failureSubscribers;
    private final ContinueCriteria<Result> continueCriteria;
    private final long timeout;

    ExecutionOfRetryable(Retryable<Result> retryable,
                         Optional<Result> defaultResult,
                         Collection<FailureSubscriber<Result>> failureSubscribers,
                         ContinueCriteria<Result> continueCriteria,
                         long timeout) {

        this.retryable = retryable;
        this.defaultResult = defaultResult;
        this.failureSubscribers = failureSubscribers;
        this.continueCriteria = continueCriteria;
        this.timeout = timeout;
    }

    Result perform() {
        PerformedWork<Result> performedWork = new PerformedWork<Result>();
        return computeResult(performedWork);
    }

    private Result computeResult(PerformedWork<Result> performedWork) {
        Optional<Result> result = obtainResultOfOneTry();
        PerformedWork<Result> workAfterCurrentTry = performedWork.tryEndedIn(result);
        if (willSuffice(workAfterCurrentTry)) {
            return workResult(workAfterCurrentTry);
        }
        handleFailure();
        return computeResult(workAfterCurrentTry);
    }

    private Optional<Result> obtainResultOfOneTry() {
        try {
            return Optional.fromNullable(retryable.tryOnce());
        } catch (Exception e) {
            return Optional.absent();
        }
    }

    private boolean willSuffice(PerformedWork<Result> performedWork) {
        return !continueCriteria.shouldBeContinuedAfter(performedWork);
    }

    private Result workResult(PerformedWork<Result> performedWork) {
        Optional<Result> workResult = performedWork.lastResult().or(defaultResult);
        if (workResult.isPresent()) {
            return workResult.get();
        }
        throw new FailedToComputeAValueException();
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
