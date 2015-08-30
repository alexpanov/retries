package me.alexpanov.retries.extras;

import java.util.concurrent.Callable;

import com.google.common.base.Preconditions;

import me.alexpanov.retries.Retryable;
import me.alexpanov.retries.annotation.Experimental;

/**
 * A Callable to Retryable bridge to allow the callables to be used without changing them.
 *
 * @since 0.0.2
 */
@Experimental
public final class CallableToRetryable<Value> implements Retryable<Value> {

    private final Callable<Value> callable;

    public CallableToRetryable(Callable<Value> callable) {
        this.callable = Preconditions.checkNotNull(callable);
    }

    @Override
    public Value tryOnce() throws Exception {
        return callable.call();
    }
}
