package me.alexpanov.retries;

import com.google.common.base.Predicate;

final class MaxRetriesNotExceededRule<Result> implements Predicate<PerformedWork<Result>> {

    private final int maxRetries;

    MaxRetriesNotExceededRule(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    @Override
    public boolean apply(PerformedWork<Result> performedWork) {
        return performedWork.numberOfTries() < maxRetries;
    }
}
