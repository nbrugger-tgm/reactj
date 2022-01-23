package com.niton.reactj.objects.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to tell a reactive Object if superclasses should be reactive too
 * <p>
 * If not present DEEP is used
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ReactiveResolution {
    enum ReactiveResolutionType {
        /**
         * Only fields of the class itself will be considered
         */
        FLAT,
        /**
         * All fields (inherited and super field) will also be included
         */
        DEEP
    }

    ReactiveResolutionType value();
}
