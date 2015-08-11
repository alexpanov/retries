package me.alexpanov.retries;

final class RetryRuntime<Result> {

    private final Retryable<Result> retryable;
    private final Options<Result> options;
    private final WorkHistory workHistory;
    private final Optional<Result> computedResult;

    RetryRuntime(Retryable<Result> retryable, Options<Result> options) {
        this(retryable, options, new WorkHistory(), new MissingOptional<Result>());
    }

    private RetryRuntime(Retryable<Result> retryable,
                         Options<Result> options,
                         WorkHistory workHistory,
                         Optional<Result> computedResult) {
        this.retryable = retryable;
        this.options = options;
        this.workHistory = workHistory;
        this.computedResult = computedResult;
    }

    boolean hasWorkToDo() {
        return !computedResult.isPresent() && !options.isSatisfiedBy(workHistory);
    }

    RetryRuntime<Result> performUnitOfWork() {
        WorkHistory updatedWorkHistory = workHistory.incrementNumberOfTries();
        Optional<Result> computedResult = computeResult();
        return new RetryRuntime<Result>(retryable, options, updatedWorkHistory, computedResult);
    }

    private Optional<Result> computeResult() {
        try {
            return new ConcreteOptional<Result>(retryable.tryOnce());
        } catch (Exception e) {
            return new MissingOptional<Result>();
        }
    }

    Result workResult() {
        Optional<Result> workResult = computedResult.or(options.defaultResult());
        if (workResult.isPresent()) {
            return workResult.value();
        }
        throw new FailedToComputeAValueException();
    }
}
