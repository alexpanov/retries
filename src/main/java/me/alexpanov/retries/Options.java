package me.alexpanov.retries;

final class Options<Result> {

    private final int maxRetries;
    private final Optional<Result> defaultResult;

    public Options() {
        this(0, new MissingOptional<Result>());
    }

    private Options(int maxRetries, Optional<Result> defaultResult) {
        this.maxRetries = maxRetries;
        this.defaultResult = defaultResult;
    }

    public Options<Result> maxRetries(int maxRetries) {
        return new Options<Result>(maxRetries, defaultResult);
    }

    public boolean isSatisfiedBy(WorkHistory workHistory) {
        return workHistory.numberOfTries() >= maxRetries;
    }

    public Options<Result> defaultResult(Optional<Result> defaultResult) {
        if (this.defaultResult.isPresent()) {
            throw new IllegalStateException("Default value has already been set: " + defaultResult.value());
        }
        return new Options<Result>(maxRetries, defaultResult);
    }

    public Optional<Result> defaultResult() {
        return defaultResult;
    }
}
