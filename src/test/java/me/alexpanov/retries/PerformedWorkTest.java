package me.alexpanov.retries;

import com.google.common.base.Optional;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class PerformedWorkTest {

    private final String lastResult = "ad";

    private final PerformedWork<String> emptyPerformedWork = new PerformedWork<String>(new StopCriteria<String>());

    @Test
    public void returnsLastResult() throws Exception {
        PerformedWork<String> performedWork = emptyPerformedWork.tryEndedIn(Optional.of(lastResult));
        assertThat(performedWork.lastResult().get()).isEqualTo(lastResult);
    }
}
