package me.alexpanov.retries;

import com.google.common.base.Predicate;

final class MatchedPredicate<Type> implements Predicate<Predicate<Type>> {

    private final Type value;

    MatchedPredicate(Type value) {
        this.value = value;
    }

    @Override
    public boolean apply(Predicate<Type> predicate) {
        return predicate.apply(value);
    }
}
