package me.alexpanov.retries;

interface Default<Result> {

    boolean isPresent();

    Result value();
}
