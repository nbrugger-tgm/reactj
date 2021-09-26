package com.niton.reactj.observer.util;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

import java.lang.reflect.Method;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class Matchers {
	private Matchers() {
	}

	//TODO: solve ... better
	public static ElementMatcher.Junction<MethodDescription> overwritesAnyOf(final Method[] abstractMethods) {
		return new ElementMatcher.Junction.AbstractBase<>() {
			@Override
			public boolean matches(MethodDescription target) {
				for (Method abstractMethod : abstractMethods) {
					if (overwrites(abstractMethod).matches(target))
						return true;
				}
				return false;
			}
		};
	}

	public static ElementMatcher.Junction<MethodDescription> overwrites(Method abstractMethod) {
		return named(abstractMethod.getName())
				.and(returns(abstractMethod.getReturnType()))
				.and(takesArguments(abstractMethod.getParameterTypes()));
	}

	public static ElementMatcher.Junction<MethodDescription> from(Class<?> type) {
		return isDeclaredBy(type).or(isOverriddenFrom(type));
	}
}
