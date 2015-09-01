package me.alexpanov.retries;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class RetriesCountTest {

    private int max = 10;

    private RetriesCount retriesCount = new RetriesCount(max);

    @Test
    public void maxIsNotReachedByDefault() throws Exception {
        assertThat(retriesCount.isMaxReached()).isFalse();
    }

    @Test
    public void maxReached() throws Exception {
        RetriesCount result = retriesCount;
        for (int i = 0; i < max; i++) {
            result = result.increment();
        }
        assertThat(result.isMaxReached()).isTrue();
    }
}
