package me.alexpanov.retries;

/**
 * @since 0.0.1
 */
class CouldNotContinueException extends RetryException {

    CouldNotContinueException(Exception cause) {
        super(cause);
    }
}
