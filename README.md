[![Build Status](https://travis-ci.org/alexpanov/retries.svg)](https://travis-ci.org/alexpanov/retries)
[![Windows Build Status](https://ci.appveyor.com/api/projects/status/c7dnnthq4ksq3960/branch/master?svg=true)](https://ci.appveyor.com/project/alexpanov/retries/branch/master)
[![Coverage](https://coveralls.io/repos/alexpanov/retries/badge.svg?branch=master&service=github)](https://coveralls.io/github/alexpanov/retries?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/me.alexpanov/retries/badge.png)](https://maven-badges.herokuapp.com/maven-central/me.alexpanov/retries/)

#Retries
You need to make a call that may fail. You need to wait for a value that meets you criteria. You write your own retry boilerplate. **Why**?
Throw it away. You need not to support it anymore.

##Build tools
Add **retries** to your dependencies. Note that *Guava is automatically added too*.
###Maven:
```xml
<dependency>
    <groupId>me.alexpanov</groupId>
    <artifactId>retries</artifactId>
    <version>0.0.1</version>
</dependency>
```
###Gradle:
```groovy
compile "me.alexpanov:retries:0.0.1"
```

##Getting started
Create a retryable:
```java
//Nasty call that returns so much nulls
Retryable<String> retryable = new Retryable<String>() {
            @Override
            public String tryOnce() throws Exception {
                return null;
            }
        };
```
###One call, one failure, RetryException is thrown
```java
String resultAfterRetries = new Retries<String>(retryable).stopOnMaxFailures(1).perform();
```
Code above will throw a subclass of **RetryException**:
```
me.alexpanov.retries.FailedToComputeAValueException
```

###One call, one failure, default value is returned
```java
String resultAfterRetries = new Retries<String>(retryable).stopOnMaxFailures(1)
                                                          .orElse("default value")
                                                          .perform();
resultAfterRetries.equals("default value"); //true
```

###Several calls, configured wait timeout
```java
String resultAfterRetries = new Retries<String>(retryable).stopOnMaxFailures(10)
                                                          .waitAfterFailureAtLeast(10, TimeUnit.SECONDS)
                                                          .orElse("default value")
                                                          .perform();
```

###Ignoring results
```java
Predicate<String> startsWithLetterB = new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                return input.startsWith("B");
            }
        };
String resultAfterRetries = new Retries<String>(retryable).stopOnMaxFailures(2)
                                                          .ignoreIfResult(startsWithLetterB)
                                                          .perform();
```

###Subscribing to failures for logging etc.
```java
FailureSubscriber<String> logTheError = new FailureSubscriber<String>() {
            @Override
            public void onFailure(RetryFailure<String> details) {
                LOG.info("Failure");
            }
        };
String resultAfterRetries = new Retries<String>(retryable).stopOnMaxFailures(10)
                                                          .onEachFailureDo(logTheError)
                                                          .perform();
```
The code above will append a log message each time a call failed (exception, null or skipped result).
                                                
##Licence
[The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt)

##Issues
Please feel free to add any issue regarding new functionality, improvements etc. in [Issues section](https://github.com/alexpanov/retries/issues)
