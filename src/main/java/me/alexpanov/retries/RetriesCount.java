package me.alexpanov.retries;

final class RetriesCount {

    private final int actual;
    private final int max;

    RetriesCount(int max) {
        this(0, max);
    }

    private RetriesCount(int actual, int max) {
        this.actual = actual;
        this.max = max;
    }

    RetriesCount increment() {
        return new RetriesCount(actual + 1, max);
    }

    boolean isMaxReached() {
        return actual >= max;
    }
}
