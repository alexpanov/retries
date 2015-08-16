package me.alexpanov.retries;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class ContinueCriteriaTest {

    private PerformedWork<String> performedWork = new PerformedWork<String>();

    private int maxRetries = 3;

    private ContinueCriteria<String> continueCriteria = new ContinueCriteria<String>().maxRetries(3);

    @Test
    public void shouldBeContinuedIfMaxRetriesNotReached() throws Exception {
        PerformedWork<String> onePerformedStep = performedWork.tryEndedIn(Optional.<String>absent());
        assertThat(continueCriteria.shouldBeContinuedAfter(onePerformedStep)).isTrue();
    }

    @Test
    public void shouldNotBeContinuedIfResultIsPresent() throws Exception {
        PerformedWork<String> onePerformedStep = performedWork.tryEndedIn(Optional.of("hello"));
        assertThat(continueCriteria.shouldBeContinuedAfter(onePerformedStep)).isFalse();
    }

    @Test
    public void shouldBeContinuedIfRuleDiscardsResult() throws Exception {
        continueCriteria = this.continueCriteria.withContinueOnResultRule(new Predicate<String>() {
            @Override
            public boolean apply(String resultOfComputation) {
                return resultOfComputation.startsWith("hel");
            }
        });
        PerformedWork<String> onePerformedStep = performedWork.tryEndedIn(Optional.of("hello"));
        assertThat(continueCriteria.shouldBeContinuedAfter(onePerformedStep)).isTrue();
    }

    @Test
    public void shouldBeStoppedAfterMaxRetries() throws Exception {
        for (int i = 0; i < maxRetries; i++) {
            performedWork = performedWork.tryEndedIn(Optional.<String>absent());
        }
        assertThat(continueCriteria.shouldBeContinuedAfter(performedWork)).isFalse();
    }
}
