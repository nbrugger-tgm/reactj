package com.niton.reactj.objects.annotations;

import com.niton.reactj.objects.ReactiveStrategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Calls to this method will trigger an update if {@link ReactiveStrategy#ANNOTATED} is used
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Reactive {
}
