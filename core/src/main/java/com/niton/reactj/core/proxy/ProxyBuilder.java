package com.niton.reactj.core.proxy;

import com.niton.reactj.api.react.ReactiveForwarder;
import com.niton.reactj.core.react.ReactiveWrapper;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.ExceptionMethod;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;

import java.lang.invoke.MethodHandles;

import static com.niton.reactj.core.proxy.ProxyCreator.*;
import static java.lang.String.format;
import static java.lang.reflect.Modifier.PRIVATE;
import static net.bytebuddy.implementation.DefaultMethodCall.prioritize;
import static net.bytebuddy.matcher.ElementMatchers.*;

public class ProxyBuilder {
	public <T> Class<? extends T> buildProxie(
			Class<? extends T> originClass,
			ElementMatcher.Junction<MethodDescription> reactiveMethod,
			ElementMatcher.Junction<MethodDescription> unreactive,
			ElementMatcher.Junction<MethodDescription> excluded,
			Module module,
			MethodHandles.Lookup lookup
	) {
		return new ByteBuddy()
				.subclass(originClass, ConstructorStrategy.Default.IMITATE_SUPER_CLASS)
				.implement(ReactiveForwarder.class)
				.name(format("%s_%s", originClass.getName(), PROXY_SUFFIX))
				.defineField(ORIGIN_FIELD, originClass, PRIVATE)
				.defineField(WRAPPER_REF, ReactiveWrapper.class, PRIVATE)

				.method(reactiveMethod)
				.intercept(prioritize(ReactiveForwarder.class))

				.method(is(getForwardTargetMethod))
				.intercept(FieldAccessor.ofField(WRAPPER_REF))

				.method(
						not(unreactive)
								.and(not(reactiveMethod))
								.and(not(is(getForwardTargetMethod)))
				)
				.intercept(
						MethodDelegation.to(ProxyForwardImpl.ToOrigin.class)
				)

				.method(isEquals())
				.intercept(
						MethodDelegation.to(ProxyForwardImpl.Equals.class)
				)

				.method(
						unreactive
								.and(not(excluded))
								.and(not(isEquals()))
				)
				.intercept(
						MethodCall.invokeSelf()
								.onField(ORIGIN_FIELD)
								.withAllArguments()
				)

				.method(excluded)
				.intercept(ExceptionMethod.throwing(CloneNotSupportedException.class))
				.make()
				.load(module.getClassLoader(), ClassLoadingStrategy.UsingLookup.of(lookup))
				.getLoaded();
	}
}
