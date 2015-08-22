package me.alexpanov.retries;

import com.google.common.base.Optional;

import org.junit.Test;

public class RetryRuntimeTest {

    private RetryRuntime<String> retryRuntime = new RetryRuntime<String>();

    @Test(expected = IllegalStateException.class)
    public void twoDefaultResultsTrow() throws Exception {
        retryRuntime.defaultResult(Optional.of("hello")).defaultResult(Optional.of("world"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultResultShouldBePresent() throws Exception {
        retryRuntime.defaultResult(Optional.<String>absent());
    }

    @Test
    public void canSpecifyOneDefaultResult() throws Exception {
        retryRuntime.defaultResult(Optional.of("hello"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void timeoutShouldNotBeNegative() throws Exception {
        retryRuntime.waitAtLeast(-1);
    }
}
