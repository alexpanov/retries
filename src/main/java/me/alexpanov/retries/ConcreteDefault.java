package me.alexpanov.retries;

final class ConcreteDefault<Result> implements Default<Result> {

    private final Result value;

    public ConcreteDefault(Result value) {
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
}
