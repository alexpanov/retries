package me.alexpanov.retries;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class MissingOptionalTest {

    private Optional<Object> missing = new MissingOptional<Object>();

    @Test
    public void missingDefaultIsNotPresent() throws Exception {
        assertThat(missing.isPresent()).isFalse();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void throwsWhenValueIsFetched() throws Exception {
        missing.value();
    }

    @Test
    public void missingOrMissingIsNotPresent() throws Exception {
        Optional<Object> combined = missing.or(new MissingOptional<Object>());
        assertThat(combined.isPresent()).isFalse();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void missingOrMissingValueThrows() throws Exception {
        missing.or(new MissingOptional<Object>()).value();
    }

    @Test
    public void missingOrConcreteValueIsPresent() throws Exception {
        Optional<Object> combined = missing.or(new ConcreteOptional<Object>(new Object()));
        assertThat(combined.isPresent()).isTrue();
    }

    @Test
    public void missingOrConcreteValueReturnsConcreteValue() throws Exception {
        Object value = new Object();
        Optional<Object> combined = missing.or(new ConcreteOptional<Object>(value));
        assertThat(combined.value()).isEqualTo(value);
    }
}
