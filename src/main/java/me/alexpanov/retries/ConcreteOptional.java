package me.alexpanov.retries;

final class ConcreteOptional<Result> implements Optional<Result> {

    private final Result value;

    public ConcreteOptional(Result value) {
        if (value == null) {
            throw new NullPointerException("Default result cannot be null");
        }

        this.value = value;
    }

    @Override
    public boolean isPresent() {
        return true;
    }

    @Override
    public Result value() {
        return value;
    }

    @Override
    public Optional<Result> or(Optional<Result> optional) {
        return this;
    }
}
