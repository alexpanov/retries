package me.alexpanov.retries;

final class Options<Result> {

    private int maxRetries;
    private Optional<Result> defaultResult = new MissingOptional<Result>();

    public void maxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public boolean isSatisfiedBy(WorkHistory workHistory) {
        return workHistory.numberOfTries() >= maxRetries;
    }

    public void defaultResult(Optional<Result> defaultResult) {
        if (this.defaultResult.isPresent()) {
            throw new IllegalStateException("Default value has already been set: " + defaultResult.value());
        }
        this.defaultResult = defaultResult;
    }

    public Optional<Result> defaultResult() {
        return defaultResult;
    }
}
