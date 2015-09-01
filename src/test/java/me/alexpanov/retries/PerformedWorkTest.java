package me.alexpanov.retries;

import com.google.common.base.Optional;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class PerformedWorkTest {

    private final String lastResult = "ad";

    private final int maxRetries = 10;
    private final PerformedWork<String> emptyPerformedWork =
            new PerformedWork<String>(new RetriesCount(maxRetries), new StopCriteria<String>());

    @Test
    public void returnsLastResult() throws Exception {
        PerformedWork<String> performedWork = emptyPerformedWork.tryEndedIn(Optional.of(lastResult));
        assertThat(performedWork.lastResult().get()).isEqualTo(lastResult);
    }

    @Test
    public void isDoneAfterMaxRetries() throws Exception {
        PerformedWork<String> performedWork = emptyPerformedWork;
        for (int i = 0; i < maxRetries; i++) {
            performedWork = performedWork.tryEndedIn(Optional.<String>absent());
        }
        assertThat(performedWork.isDone()).isTrue();
    }

    @Test
    public void almostDoneIsNotDone() throws Exception {
        PerformedWork<String> performedWork = emptyPerformedWork;
        for (int i = 0; i < maxRetries - 1; i++) {
            performedWork = performedWork.tryEndedIn(Optional.<String>absent());
        }
        assertThat(performedWork.isDone()).isFalse();
    }

    @Test
    public void emptyWorkIsNotDone() throws Exception {
        assertThat(emptyPerformedWork.isDone()).isFalse();
    }
}
