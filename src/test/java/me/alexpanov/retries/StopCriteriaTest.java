package me.alexpanov.retries;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class StopCriteriaTest {

    private int maxRetries = 3;

    private StopCriteria<String> stopCriteria = new StopCriteria<String>().maxRetries(3);
    private PerformedWork<String> performedWork = new PerformedWork<String>(stopCriteria);

    @Test
    public void shouldBeContinuedIfMaxRetriesNotReached() throws Exception {
        PerformedWork<String> onePerformedStep = performedWork.tryEndedIn(Optional.<String>absent());
        assertThat(stopCriteria.shouldBeContinuedAfter(onePerformedStep)).isTrue();
    }

    @Test
    public void shouldNotBeContinuedIfResultIsPresent() throws Exception {
        PerformedWork<String> onePerformedStep = performedWork.tryEndedIn(Optional.of("hello"));
        assertThat(stopCriteria.shouldBeContinuedAfter(onePerformedStep)).isFalse();
    }

    @Test
    public void shouldBeContinuedIfRuleDiscardsResult() throws Exception {
        stopCriteria = this.stopCriteria.withContinueOnResultRule(new Predicate<String>() {
            @Override
            public boolean apply(String resultOfComputation) {
                return resultOfComputation.startsWith("hel");
            }
        });
        PerformedWork<String> onePerformedStep = performedWork.tryEndedIn(Optional.of("hello"));
        assertThat(stopCriteria.shouldBeContinuedAfter(onePerformedStep)).isTrue();
    }

    @Test
    public void shouldBeStoppedAfterMaxRetries() throws Exception {
        for (int i = 0; i < maxRetries; i++) {
            performedWork = performedWork.tryEndedIn(Optional.<String>absent());
        }
        assertThat(stopCriteria.shouldBeContinuedAfter(performedWork)).isFalse();
    }
}
