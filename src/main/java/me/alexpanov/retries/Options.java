package me.alexpanov.retries;

final class Options {

    private int maxRetries;

    public void maxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public int maxRetries() {
        return maxRetries;
    }
}
