package me.alexpanov.retries;

import com.google.common.base.Predicate;

final class Match<Type> implements Predicate<Predicate<Type>> {

    private final Type value;

    Match(Type value) {
        this.value = value;
    }

    @Override
    public boolean apply(Predicate<Type> predicate) {
        return predicate.apply(value);
    }
}
