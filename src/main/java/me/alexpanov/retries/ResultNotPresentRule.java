package me.alexpanov.retries;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;

final class ResultNotPresentRule<Result> implements Predicate<Optional<Result>> {

    @Override
    public boolean apply(Optional<Result> input) {
        return !input.isPresent();
    }
}
