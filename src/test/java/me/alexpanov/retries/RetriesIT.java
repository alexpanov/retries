package me.alexpanov.retries;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Future;

import com.google.common.base.Stopwatch;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import static com.google.common.base.Predicates.containsPattern;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;

public class RetriesIT {

    private Random random = new Random();
    private int sleepTimeout = random.nextInt(10) + 10;
    private int maxRetries = random.nextInt(10) + 10;
    private String result = UUID.randomUUID().toString();

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
                return result;
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
        performIgnoringException(retries);
        assertThat(stopwatch.stop().elapsed(MILLISECONDS)).isGreaterThanOrEqualTo(expectedSleepTime());
    }

    private void performIgnoringException(Retries<String> retries) {
        try {
            retries.perform();
            fail();
        } catch (FailedToComputeAValueException ignored) {
        }
    }

    private Retryable<String> alwaysReturn(final String dumbData) {
        return new Retryable<String>() {
            @Override
            public String tryOnce() throws Exception {
                return dumbData;
            }
        };
    }

    @Test
    public void futureExecutionReturnsImmidiately() throws Exception {
        Retries<String> retries = new Retries<String>(failTillLastTry()).stopOnMaxFailures(maxRetries)
                                                                        .waitAfterFailureAtLeast(sleepTimeout,
                                                                                                 MILLISECONDS);
        Stopwatch stopwatch = Stopwatch.createStarted();
        retries.performAsync();
        assertThat(stopwatch.stop().elapsed(MILLISECONDS)).isLessThanOrEqualTo(sleepTimeout / 2);
    }

    @Test
    public void futureExecutionReturnsExpectedValue() throws Exception {
        Retries<String> retries = new Retries<String>(failTillLastTry()).stopOnMaxFailures(maxRetries)
                                                                        .waitAfterFailureAtLeast(sleepTimeout,
                                                                                                 MILLISECONDS);
        Future<String> future = retries.performAsync();
        assertThat(future.get()).isEqualTo(result);
    }
}
