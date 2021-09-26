package com.niton.reactj.core.annotation;

import com.niton.reactj.api.mvc.ReactiveComponent;

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
 *         <b>Methods:</b> Adds an automatic binding<br><i>ONLY WORKS IN {@link ReactiveComponent}</i>
 *     </li>
 * </ul>
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Reactive {
	String value();
}
