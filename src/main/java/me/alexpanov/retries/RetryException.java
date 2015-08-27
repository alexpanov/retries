package me.alexpanov.retries;

/**
 * Base exception that may be thrown while retries are carried out
 *
 * @see CouldNotContinueException
 * @see FailedToComputeAValueException
 */
public class RetryException extends RuntimeException {

    RetryException() {
    }

    RetryException(Exception cause) {
        super(cause);
    }
}
