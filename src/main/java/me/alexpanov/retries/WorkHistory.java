package me.alexpanov.retries;

final class WorkHistory {

    private final int numberOfTries;

    WorkHistory() {
        this(0);
    }

    private WorkHistory(int numberOfTries) {
        this.numberOfTries = numberOfTries;
    }

    public WorkHistory incrementNumberOfTries() {
        return new WorkHistory(numberOfTries + 1);
    }

    public int numberOfTries() {
        return numberOfTries;
    }
}
