package me.alexpanov.retries;

import com.google.common.base.Optional;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class WorkHistoryTest {

    private final String lastResult = "ad";

    private final WorkHistory<String> emptyWorkHistory = new WorkHistory<String>();

    @Test
    public void returnsLastResult() throws Exception {
        WorkHistory<String> workHistory = emptyWorkHistory.tryEndedIn(Optional.of(lastResult));
        assertThat(workHistory.lastResult().get()).isEqualTo(lastResult);
    }
}
