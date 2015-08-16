package me.alexpanov.retries;

import java.util.Collection;

import com.google.common.base.Optional;

final class ExecutionOfRetryable<Result> {

    private final Retryable<Result> retryable;
    private final Options<Result> options;
    private final PerformedWork<Result> performedWork;
    private final Optional<Result> computedResult;

    ExecutionOfRetryable(Retryable<Result> retryable, Options<Result> options) {
        this(retryable, options, new PerformedWork<Result>(), Optional.<Result>absent());
    }

    ExecutionOfRetryable(Retryable<Result> retryable,
                         Options<Result> options,
                         PerformedWork<Result> performedWork,
                         Optional<Result> computedResult) {
        this.retryable = retryable;
        this.options = options;
        this.performedWork = performedWork;
        this.computedResult = computedResult;
    }

    boolean hasWorkToDo() {
        return !options.isSatisfiedBy(performedWork);
    }

    ExecutionOfRetryable<Result> performUnitOfWork() {
        Optional<Result> computedResult = computeResult();
        PerformedWork<Result> updatedPerformedWork = performedWork.tryEndedIn(computedResult);
        return new ExecutionOfRetryable<Result>(retryable, options, updatedPerformedWork, computedResult);
    }

    private Optional<Result> computeResult() {
        try {
            return Optional.of(retryable.tryOnce());
        } catch (Exception e) {
            Collection<FailureSubscriber> failureSubscribers = options.failureSubscribers();
            for (FailureSubscriber failureSubscriber : failureSubscribers) {
                failureSubscriber.handle();
            }
            return Optional.absent();
        }
    }

    Result workResult() {
        Optional<Result> workResult = computedResult.or(options.defaultResult());
        if (workResult.isPresent()) {
            return workResult.get();
        }
        throw new FailedToComputeAValueException();
    }
}
