package me.alexpanov.retries;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class ConcreteDefaultTest {

    @Test(expected = NullPointerException.class)
    public void throwsOnNull() throws Exception {
        new ConcreteDefault<Object>(null);
    }

    @Test
    public void returnsValue() throws Exception {
        Object value = new Object();
        ConcreteDefault<Object> aDefault = new ConcreteDefault<Object>(value);
        assertThat(aDefault.value()).isEqualTo(value);
    }

    @Test
    public void valueIsPresentIfNotNull() throws Exception {
        Object value = new Object();
        ConcreteDefault<Object> aDefault = new ConcreteDefault<Object>(value);
        assertThat(aDefault.isPresent()).isTrue();
    }
}
