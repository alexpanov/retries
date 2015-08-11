package me.alexpanov.retries;

final class MissingDefault<Result> implements Default<Result> {

    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    public Result value() {
        throw new UnsupportedOperationException("Cannot fetch a missing value");
    }
}
