package me.alexpanov.retries;

import com.google.common.base.Predicate;

final class ResultNotPresentRule<Result> implements Predicate<PerformedWork<Result>> {

    @Override
    public boolean apply(PerformedWork<Result> input) {
        return !input.lastResult().isPresent();
    }
}
