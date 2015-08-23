package me.alexpanov.retries;

public interface FailureSubscriber {

    void onFailure(RetryFailure details);
}
