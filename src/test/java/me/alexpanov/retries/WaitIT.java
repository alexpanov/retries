package me.alexpanov.retries;

import java.util.Random;

import com.google.common.base.Stopwatch;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.fest.assertions.Assertions.assertThat;

public class WaitIT {

    @Rule
    public Timeout timeout = Timeout.seconds(1);

    private final int timeoutValue = new Random().nextInt(20) + 20;

    @Test
    public void waitsAtLeastSpecifiedTime() throws Exception {
        Stopwatch stopwatch = Stopwatch.createStarted();
        new Wait(timeoutValue).perform();
        assertThat(stopwatch.stop().elapsed(MILLISECONDS)).isGreaterThanOrEqualTo(timeoutValue);
    }

    @Test(expected = CouldNotContinueException.class)
    public void interruptedWait() throws Exception {
        Thread.currentThread().interrupt();
        new Wait(timeoutValue).perform();
    }
}
