package me.alexpanov.retries;

final class MissingOptional<Result> implements Optional<Result> {

    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    public Result value() {
        throw new UnsupportedOperationException("Cannot fetch a missing value");
    }

    @Override
    public Optional<Result> or(Optional<Result> optional) {
        return optional;
    }
}
