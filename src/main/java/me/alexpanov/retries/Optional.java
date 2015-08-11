package me.alexpanov.retries;

interface Optional<Result> {

    boolean isPresent();

    Result value();

    Optional<Result> or(Optional<Result> optional);
}
