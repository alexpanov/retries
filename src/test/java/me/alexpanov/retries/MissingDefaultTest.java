package me.alexpanov.retries;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class MissingDefaultTest {

    private Default<Object> missing = new MissingDefault<Object>();

    @Test
    public void missingDefaultIsNotPresent() throws Exception {
        assertThat(missing.isPresent()).isFalse();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void throwsWhenValueIsFetched() throws Exception {
        missing.value();
    }
}
