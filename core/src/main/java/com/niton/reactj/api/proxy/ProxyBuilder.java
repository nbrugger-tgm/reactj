package com.niton.reactj.api.proxy;

import com.niton.reactj.api.react.ReactiveForwarder;
import com.niton.reactj.api.react.ReactiveWrapper;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.ExceptionMethod;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;

import static com.niton.reactj.api.proxy.ProxyCreator.*;
import static java.lang.String.format;
import static java.lang.reflect.Modifier.PRIVATE;
import static net.bytebuddy.implementation.DefaultMethodCall.prioritize;
import static net.bytebuddy.matcher.ElementMatchers.*;

public class ProxyBuilder {
	public <T> DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends T> buildProxie(
			Class<? extends T> originClass,
			ElementMatcher.Junction<MethodDescription> reactiveMethod,
			ElementMatcher.Junction<MethodDescription> unreactive,
			ElementMatcher.Junction<MethodDescription> excluded
	) {
		return new ByteBuddy()
				.subclass(originClass, ConstructorStrategy.Default.IMITATE_SUPER_CLASS)
				.implement(ReactiveForwarder.class)
				.name(format("%s_%s", originClass.getName(), ProxyCreator.PROXY_SUFFIX))
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
				.intercept(ExceptionMethod.throwing(CloneNotSupportedException.class));
	}
}
