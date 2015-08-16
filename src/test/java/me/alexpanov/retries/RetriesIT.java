package me.alexpanov.retries;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import static org.fest.assertions.Assertions.assertThat;

public class RetriesIT {

    private Random random = new Random();
    private int sleepTimeout = random.nextInt(10) + 10;
    private int maxRetries = random.nextInt(10) + 10;

    @Rule
    public Timeout timeout = Timeout.seconds(1);

    @Test
    public void sleepsAtLeastTheSpecifiedAmountOfTime() throws Exception {
        Stopwatch stopwatch = Stopwatch.createStarted();
        new Retries<String>(failTillLastTry()).stopOnMaxFailures(maxRetries)
                                              .waitAfterFailuresAtLeast(sleepTimeout, TimeUnit.MILLISECONDS)
                                              .perform();
        assertThat(stopwatch.stop().elapsed(TimeUnit.MILLISECONDS)).isGreaterThanOrEqualTo(sleepTimeout * maxRetries);
    }

    private Retryable<String> failTillLastTry() {
        return new Retryable<String>() {
            private int timesCalled = 0;

            @Override
            public String tryOnce() throws Exception {
                timesCalled++;
                if (timesCalled < maxRetries) {
                    throw new IllegalStateException();
                }
                return "Done";
            }
        };
    }
}
