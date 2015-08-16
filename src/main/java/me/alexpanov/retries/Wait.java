package me.alexpanov.retries;

final class Wait {

    private final long timeout;

    Wait(long timeout) {
        this.timeout = timeout;
    }

    void perform() {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CouldNotContinueException(e);
        }
    }
}
