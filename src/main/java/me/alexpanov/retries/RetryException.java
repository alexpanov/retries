package me.alexpanov.retries;

public class RetryException extends RuntimeException {

    RetryException() {
    }

    RetryException(Exception cause) {
        super(cause);
    }
}
