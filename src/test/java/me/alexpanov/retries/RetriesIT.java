package me.alexpanov.retries;

import java.util.Random;

import com.google.common.base.Stopwatch;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import static com.google.common.base.Predicates.containsPattern;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.fest.assertions.Assertions.assertThat;

public class RetriesIT {

    private Random random = new Random();
    private int sleepTimeout = random.nextInt(10) + 10;
    private int maxRetries = random.nextInt(10) + 10;

    @Rule
    public Timeout timeout = Timeout.seconds(10);

    @Test
    public void sleepsAtLeastTheSpecifiedAmountOfTime() throws Exception {
        Stopwatch stopwatch = Stopwatch.createStarted();
        new Retries<String>(failTillLastTry()).stopOnMaxFailures(maxRetries)
                                              .waitAfterFailureAtLeast(sleepTimeout, MILLISECONDS)
                                              .perform();
        assertThat(stopwatch.stop().elapsed(MILLISECONDS)).isGreaterThanOrEqualTo(expectedSleepTime());
    }

    private long expectedSleepTime() {
        return sleepTimeout * maxRetries / 2;
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

    @Test
    public void shouldWaitAfterResultNotMatched() throws Exception {
        String dumbData = "hello";
        Retries<String> retries = new Retries<String>(alwaysReturn(dumbData)).stopOnMaxFailures(maxRetries)
                                                                             .ignoreIfResult(containsPattern(dumbData))
                                                                             .waitAfterFailureAtLeast(sleepTimeout,
                                                                                                      MILLISECONDS);
        Stopwatch stopwatch = Stopwatch.createStarted();
        retries.perform();
        assertThat(stopwatch.stop().elapsed(MILLISECONDS)).isGreaterThanOrEqualTo(expectedSleepTime());
    }

    private Retryable<String> alwaysReturn(final String dumbData) {
        return new Retryable<String>() {
            @Override
            public String tryOnce() throws Exception {
                return dumbData;
            }
        };
    }
}
