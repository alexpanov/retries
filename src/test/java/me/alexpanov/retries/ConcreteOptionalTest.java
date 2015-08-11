package me.alexpanov.retries;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class ConcreteOptionalTest {

    private final Object value = new Object();
    private final ConcreteOptional<Object> optional = new ConcreteOptional<Object>(value);

    @Test(expected = NullPointerException.class)
    public void throwsOnNull() throws Exception {
        new ConcreteOptional<Object>(null);
    }

    @Test
    public void returnsValue() throws Exception {
        assertThat(optional.value()).isEqualTo(value);
    }

    @Test
    public void valueIsPresentIfNotNull() throws Exception {
        assertThat(optional.isPresent()).isTrue();
    }

    @Test
    public void thisOrOtherReturnsThis() throws Exception {
        Optional<Object> combined = optional.or(new ConcreteOptional<Object>(new Object()));
        assertThat(combined.value()).isEqualTo(value);
    }
}
