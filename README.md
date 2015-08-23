[![Build Status](https://travis-ci.org/alexpanov/retries.svg)](https://travis-ci.org/alexpanov/retries)[![Windows Build Status](https://ci.appveyor.com/api/projects/status/c7dnnthq4ksq3960/branch/master?svg=true)](https://ci.appveyor.com/project/alexpanov/retries/branch/master)[![Coverage](https://coveralls.io/repos/alexpanov/retries/badge.svg?branch=master&service=github)](https://coveralls.io/github/alexpanov/retries?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/me.alexpanov/retries/badge.png)](https://maven-badges.herokuapp.com/maven-central/me.alexpanov/retries/)

Java 6 and 7:
```java
Retryable<String> retryable = new Retryable<String>() {
            @Override
            public String tryOnce() throws Exception {
                return "hello";
            }
        };
String resultAfterRetries = new Retries<String>(retryable).stopOnMaxFailures(1)
                                                          .waitAfterFailureAtLeast(10, TimeUnit.SECONDS)
                                                          .onEachFailureDo(new LogTheError())
                                                          .ignoreIfResult(new StartsWithLetterB())
                                                          .orElse("default value")
                                                          .perform();

```

Java 8:
```java
Retryable<String> retryable = () -> "hello";
String resultAfterRetries = new Retries<String>(retryable).stopOnMaxFailures(1)
                                                          .waitAfterFailureAtLeast(10, TimeUnit.SECONDS)
                                                          .onEachFailureDo(new LogTheError())
                                                          .ignoreIfResult(new StartsWithLetterB())
                                                          .orElse("default value")
                                                          .perform();

```
