[![Build Status](https://travis-ci.org/alexpanov/retries.svg)](https://travis-ci.org/alexpanov/retries)[![Coverage](https://coveralls.io/repos/alexpanov/retries/badge.svg?branch=master&service=github)](https://coveralls.io/github/alexpanov/retries?branch=master)

```java
Retryable<Object> retryable = new Retryable<Object>() {
    @Override
    public Object tryOnce() throws Exception {
        return new Object();
    }
};

Object result = new Retries<Object>(retryable).perform();

```
