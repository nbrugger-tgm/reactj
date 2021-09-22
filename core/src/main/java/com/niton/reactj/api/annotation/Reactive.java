package com.niton.reactj.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used for two purposes:
 * <ul>
 *     <li>
 *         <b>Fields:</b> Changing the name of a field
 *     </li>
 *     <li>
 *         <b>Methods:</b> Adds an automatic binding<br><i>ONLY WORKS IN {@link com.niton.reactj.api.react.ReactiveComponent}</i>
 *     </li>
 * </ul>
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Reactive {
	String value();
}
