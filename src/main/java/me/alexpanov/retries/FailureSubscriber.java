package me.alexpanov.retries;

/**
 * Retry failure subscriber that recieves individual failure events
 *
 * @since 0.0.1
 */
public interface FailureSubscriber<Result> {

    void onFailure(RetryFailure<Result> details);
}
