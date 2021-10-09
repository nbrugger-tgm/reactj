package com.niton.reactj.core.react;

import com.niton.reactj.core.annotation.Reactive;
import com.niton.reactj.core.annotation.Unreactive;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * Defines methods that should be reacted to
 */
public enum ReactiveStrategy {
	/**
	 * Only react to methods that start with "set". Methods with {@link Unreactive} will not be reacted to
	 */
	SETTERS(ElementMatchers.nameStartsWith("set").and(not(isAnnotatedWith(Unreactive.class)))),
	/**
	 * Reacts to all method calls that are not {@link Unreactive}
	 */
	ALL(ElementMatchers.not(isAnnotatedWith(Unreactive.class))),
	/**
	 * Reacts to no method calls at all
	 */
	NOTHING(ElementMatchers.none()),
	/**
	 * Reacts to methods that are annotated with {@link Reactive}
	 */
	ANNOTATED(ElementMatchers.isAnnotatedWith(Reactive.class).and(not(isAnnotatedWith(Unreactive.class))));
	public final ElementMatcher.Junction<MethodDescription> matcher;


	ReactiveStrategy(ElementMatcher.Junction<MethodDescription> matcher) {
		this.matcher = matcher;
	}
}
