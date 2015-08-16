package me.alexpanov.retries;

import java.util.Collections;
import java.util.Deque;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import static com.google.common.collect.Lists.newLinkedList;

final class ContinueCriteria<Result> {

    private final Deque<Predicate<PerformedWork<Result>>> rules;

    private final int maxRetries;

    ContinueCriteria() {
        this(2, Collections.<Predicate<PerformedWork<Result>>>singleton(new ResultNotPresentRule<Result>()));
    }

    private ContinueCriteria(int maxRetries, Iterable<Predicate<PerformedWork<Result>>> rules) {
        this.maxRetries = maxRetries;
        this.rules = newLinkedList(rules);
    }

    ContinueCriteria<Result> withContinueRule(Predicate<PerformedWork<Result>> rule) {
        Deque<Predicate<PerformedWork<Result>>> newRules = newLinkedList(rules);
        newRules.addFirst(rule);
        return new ContinueCriteria<Result>(maxRetries, newRules);
    }

    boolean shouldBeContinuedAfter(final PerformedWork<Result> performedWork) {
        if (performedWork.numberOfTries() >= maxRetries) {
            return false;
        }
        return anyRuleMatches(performedWork);
    }

    private boolean anyRuleMatches(final PerformedWork<Result> performedWork) {
        Optional<Predicate<PerformedWork<Result>>> aMatch =
                FluentIterable.from(rules).firstMatch(new MatchedPredicate<PerformedWork<Result>>(performedWork));
        return aMatch.isPresent();
    }

    ContinueCriteria<Result> withContinueOnResultRule(Predicate<? super Result> continueOnResultRule) {
        Predicate<PerformedWork<Result>> newRule = toPerformedWorkRule(continueOnResultRule);
        return withContinueRule(newRule);
    }

    private Predicate<PerformedWork<Result>> toPerformedWorkRule(final Predicate<? super Result> continueOnResultRule) {
        return new Predicate<PerformedWork<Result>>() {
            @Override
            public boolean apply(PerformedWork<Result> performedWork) {
                Optional<Result> lastResult = performedWork.lastResult();
                return lastResult.isPresent() && continueOnResultRule.apply(lastResult.get());
            }
        };
    }

    ContinueCriteria<Result> maxRetries(int maxRetries) {
        return new ContinueCriteria<Result>(maxRetries, rules);
    }
}
