package com.niton.reactj.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to tell a reactive Object if superclasses should be reactive too
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ReactivResolution {
	enum ReactiveResolutions {
		/**
		 * Only fields of the class itself will be considered
		 */
		FLAT,
		/**
		 * All fields (inherited and super field) will also be included
		 */
		DEEP
	}

	ReactiveResolutions value();
}
