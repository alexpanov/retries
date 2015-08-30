package me.alexpanov.retries.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Marks certain library parts as experimental i.e. subject to deletion in future versions.
 * Use marked library parts at your own risk.
 */
@Target(value = {CONSTRUCTOR, FIELD, METHOD, TYPE})
@Retention(CLASS)
public @interface Experimental {

}
