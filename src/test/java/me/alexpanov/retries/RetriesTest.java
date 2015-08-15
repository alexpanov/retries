package me.alexpanov.retries;

import com.google.common.base.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RetriesTest {

    @Mock
    private Retryable<Object> retryable;

    private Retries<Object> retries;

    private String expectedResult = new RandomStrings().createOne();

    @Before
    public void createRetries() throws Exception {
        retries = new Retries<Object>(retryable);
    }

    @Test
    public void retriesOnFailure() throws Exception {
        when(retryable.tryOnce()).thenThrow(IllegalArgumentException.class).thenReturn(expectedResult);
        Object result = retries.stopOnMaxFailures(2).perform();
        assertThat(result).isEqualTo(expectedResult);
        verify(retryable, times(2)).tryOnce();
    }

    @Test
    public void usesResult() throws Exception {
        when(retryable.tryOnce()).thenReturn(expectedResult);
        Object result = retries.stopOnMaxFailures(2).perform();
        assertThat(result).isEqualTo(expectedResult);
        verify(retryable).tryOnce();
    }

    @Test(expected = FailedToComputeAValueException.class)
    public void onMaxAttemptsFailedThrows() throws Exception {
        when(retryable.tryOnce()).thenThrow(IllegalArgumentException.class);
        retries.stopOnMaxFailures(2).perform();
        verify(retryable, times(2)).tryOnce();
    }

    @Test(expected = FailedToComputeAValueException.class)
    public void catchesCheckedExceptions() throws Exception {
        new Retries<Object>(new CheckedExceptionThrowingRetryable()).stopOnMaxFailures(2).perform();
    }

    @Test
    public void usesDefaultValue() throws Exception {
        when(retryable.tryOnce()).thenThrow(IllegalArgumentException.class);

        Object result = retries.stopOnMaxFailures(1).orElse(expectedResult).perform();
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test(expected = NullPointerException.class)
    public void throwsOnNullDefaultValue() throws Exception {
        retries.orElse(null);
    }

    @Test(expected = IllegalStateException.class)
    public void throwsOnTwoDefaultValues() throws Exception {
        retries.orElse(expectedResult).orElse("Hello");
    }

    @Test
    public void ignoreResult() throws Exception {
        when(retryable.tryOnce()).thenReturn(new Object()).thenReturn(expectedResult);
        Object result = retries.ignoreIfResult(isNotExpectedResult()).perform();
        assertThat(result).isEqualTo(expectedResult);
    }

    private Predicate<Object> isNotExpectedResult() {
        return new Predicate<Object>() {
            @Override
            public boolean apply(Object input) {
                return input != expectedResult;
            }
        };
    }
}
