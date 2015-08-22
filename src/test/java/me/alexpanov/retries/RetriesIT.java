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
    private int sleepTimeout = random.nextInt(10) + 5;
    private int maxRetries = random.nextInt(10) + 5;

    @Rule
    public Timeout timeout = Timeout.seconds(10);

    @Test
    public void sleepsAtLeastTheSpecifiedAmountOfTime() throws Exception {
        Retries<String> retries = new Retries<String>(failTillLastTry()).stopOnMaxFailures(maxRetries)
                                                                        .waitAfterFailureAtLeast(sleepTimeout,
                                                                                                 MILLISECONDS);
        Stopwatch stopwatch = Stopwatch.createStarted();
        retries.perform();
        long elapsed = stopwatch.stop().elapsed(MILLISECONDS);
        int other = sleepTimeout * maxRetries;
        System.out.println(elapsed);
        System.out.println(other);
        assertThat(elapsed).isGreaterThanOrEqualTo(other);
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
                System.out.println(sleepTimeout);
                System.out.println(maxRetries);
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
        assertThat(stopwatch.stop().elapsed(MILLISECONDS)).isGreaterThanOrEqualTo(sleepTimeout * maxRetries);
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
