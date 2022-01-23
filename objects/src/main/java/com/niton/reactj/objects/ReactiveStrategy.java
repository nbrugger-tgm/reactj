package com.niton.reactj.objects;

import com.niton.reactj.objects.annotations.Reactive;
import com.niton.reactj.objects.annotations.Unreactive;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * Defines methods that should be reacted to
 */
public enum ReactiveStrategy {
    /**
     * Only react to methods that start with "set". Methods with {@link Unreactive} will not be
     * reacted to
     */
    SETTERS(ElementMatchers.nameStartsWith("set")
                           .and(ElementMatchers.not(ElementMatchers.isAnnotatedWith(Unreactive.class)))),
    /**
     * Reacts to all method calls that are not {@link Unreactive}
     */
    ALL(ElementMatchers.not(ElementMatchers.isAnnotatedWith(Unreactive.class))),
    /**
     * Reacts to no method calls at all
     */
    NOTHING(ElementMatchers.none()),
    /**
     * Reacts to methods that are annotated with {@link Reactive}
     */
    ANNOTATED(ElementMatchers.isAnnotatedWith(Reactive.class)
                             .and(ElementMatchers.not(ElementMatchers.isAnnotatedWith(Unreactive.class))));
    public final ElementMatcher.Junction<MethodDescription> matcher;


    ReactiveStrategy(ElementMatcher.Junction<MethodDescription> matcher) {
        this.matcher = matcher;
    }
}
