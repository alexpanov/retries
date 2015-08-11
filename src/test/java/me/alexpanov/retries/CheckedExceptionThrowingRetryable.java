package me.alexpanov.retries;

final class CheckedExceptionThrowingRetryable implements Retryable<Object> {

    @Override
    public Object tryOnce() throws CheckedException {
        throw new CheckedException();
    }

    static class CheckedException extends Exception {

    }
}
