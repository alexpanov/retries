package me.alexpanov.retries;

public final class Retries<Result> {

    private final Retryable<Result> retryable;

    private Default<Result> defaultResult = new MissingDefault<Result>();

    private final Options options;

    public Retries(Retryable<Result> retryable) {
        this.retryable = retryable;
        this.options = new Options();
    }

    public Retries<Result> stopOnMaxFailures(int maxRetries) {
        options.maxRetries(maxRetries);
        return this;
    }

    public Result perform() throws FailedAfterMaxAttemptsException {
        for (int i = 0; i < options.maxRetries(); i++) {
            try {
                return retryable.tryOnce();
            } catch (Exception ignored) {
            }
        }
        if (defaultResult.isPresent()) {
            return defaultResult.value();
        }

        throw new FailedAfterMaxAttemptsException();
    }

    public Retries<Result> orElse(Result value) {
        if (defaultResult.isPresent()) {
            throw new IllegalStateException("Default value has already been set: " + defaultResult.value());
        }
        this.defaultResult = new ConcreteDefault<Result>(value);
        return this;
    }
}
