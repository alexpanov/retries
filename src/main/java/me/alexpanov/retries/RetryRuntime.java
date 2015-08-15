package me.alexpanov.retries;

import com.google.common.base.Optional;

final class RetryRuntime<Result> {

    private final Retryable<Result> retryable;
    private final Options<Result> options;
    private final WorkHistory<Result> workHistory;
    private final Optional<Result> computedResult;

    RetryRuntime(Retryable<Result> retryable, Options<Result> options) {
        this(retryable, options, new WorkHistory<Result>(), Optional.<Result>absent());
    }

    private RetryRuntime(Retryable<Result> retryable,
                         Options<Result> options, WorkHistory<Result> workHistory,
                         Optional<Result> computedResult) {
        this.retryable = retryable;
        this.options = options;
        this.workHistory = workHistory;
        this.computedResult = computedResult;
    }

    boolean hasWorkToDo() {
        return !options.isSatisfiedBy(workHistory);
    }

    RetryRuntime<Result> performUnitOfWork() {
        Optional<Result> computedResult = computeResult();
        WorkHistory<Result> updatedWorkHistory = workHistory.tryEndedIn(computedResult);
        return new RetryRuntime<Result>(retryable, options, updatedWorkHistory, computedResult);
    }

    private Optional<Result> computeResult() {
        try {
            return Optional.of(retryable.tryOnce());
        } catch (Exception e) {
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
