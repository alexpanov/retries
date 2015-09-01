package me.alexpanov.retries;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class StopCriteriaTest {

    private StopCriteria<String> stopCriteria = new StopCriteria<String>().maxRetries(3);

    @Test
    public void emptyResultNotMatches() throws Exception {
        assertThat(stopCriteria.shouldContinue(Optional.<String>absent())).isTrue();
    }

    @Test
    public void noMatch() throws Exception {
        Optional<String> hello = Optional.of("hello");
        assertThat(stopCriteria.shouldContinue(hello)).isFalse();
    }

    @Test
    public void hasAMatch() throws Exception {
        stopCriteria = this.stopCriteria.withContinueOnResultRule(new Predicate<String>() {
            @Override
            public boolean apply(String resultOfComputation) {
                return resultOfComputation.startsWith("hel");
            }
        });
        Optional<String> hello = Optional.of("hello");
        assertThat(stopCriteria.shouldContinue(hello)).isTrue();
    }
}
