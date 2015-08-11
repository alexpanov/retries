package me.alexpanov.retries;

public interface Retryable<Result> {

    Result tryOnce() throws Exception;
}
