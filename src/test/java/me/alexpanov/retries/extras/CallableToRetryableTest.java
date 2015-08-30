package me.alexpanov.retries.extras;

import java.util.concurrent.Callable;

import com.google.common.testing.NullPointerTester;

import org.junit.Test;

import me.alexpanov.retries.Retryable;

import static org.fest.assertions.Assertions.assertThat;

public class CallableToRetryableTest {

    private Object value = new Object();

    private Callable<Object> callable = new Callable<Object>() {
        @Override
        public Object call() throws Exception {
            return value;
        }
    };

    @Test
    public void proxiesCalls() throws Exception {
        Retryable<Object> retryable = new CallableToRetryable<Object>(callable);
        assertThat(retryable.tryOnce()).isEqualTo(value);
    }

    @Test
    public void constructorThrowsOnNull() throws Exception {
        new NullPointerTester().testAllPublicConstructors(CallableToRetryable.class);
    }
}
