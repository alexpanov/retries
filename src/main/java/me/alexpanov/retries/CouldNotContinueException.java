package me.alexpanov.retries;

class CouldNotContinueException extends RetryException {

    CouldNotContinueException(Exception cause) {
        super(cause);
    }
}
