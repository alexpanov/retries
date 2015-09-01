package me.alexpanov.retries;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

final class StopCriteria<Result> {

    private final Predicate<Optional<Result>> rules;
    private final int maxRetries;

    StopCriteria() {
        this(2, new ResultNotPresentRule<Result>());
    }

    private StopCriteria(int maxRetries, Predicate<Optional<Result>> rules) {
        this.maxRetries = maxRetries;
        this.rules = rules;
    }

    boolean shouldContinue(Optional<Result> result) {
        return rules.apply(result);
    }

    StopCriteria<Result> withContinueOnResultRule(Predicate<? super Result> continueOnResultRule) {
        Predicate<Optional<Result>> newRule = toOptionalResultRule(continueOnResultRule);
        return withContinueRule(newRule);
    }

    private Predicate<Optional<Result>> toOptionalResultRule(final Predicate<? super Result> continueOnResultRule) {
        return new Predicate<Optional<Result>>() {
            @Override
            public boolean apply(Optional<Result> lastResult) {
                return continueOnResultRule.apply(lastResult.get());
            }
        };
    }

    private StopCriteria<Result> withContinueRule(Predicate<Optional<Result>> rule) {
        Predicate<Optional<Result>> newRules = Predicates.or(rules, rule);
        return new StopCriteria<Result>(maxRetries, newRules);
    }

    StopCriteria<Result> maxRetries(int maxRetries) {
        return new StopCriteria<Result>(maxRetries, rules);
    }

    PerformedWork<Result> startNewWork() {
        return new PerformedWork<Result>(new RetriesCount(maxRetries), this);
    }
}
