[![Build Status](https://travis-ci.org/alexpanov/retries.svg)](https://travis-ci.org/alexpanov/retries)

```java
Retryable<Object> retryable = new Retryable<Object>() {
    @Override
    public Object tryOnce() throws Exception {
        return new Object();
    }
};

Object result = new Retries<Object>(retryable).perform();

```
