package com.niton.reactj.api.proxy;

import com.niton.reactj.api.observer.Reactable;
import com.niton.reactj.api.proxy.infusion.InfusionAccessProvider;
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

import java.lang.reflect.Method;

import static com.niton.reactj.observer.util.Matchers.from;
import static java.lang.String.format;
import static java.lang.reflect.Modifier.PRIVATE;
import static net.bytebuddy.implementation.DefaultMethodCall.prioritize;
import static net.bytebuddy.matcher.ElementMatchers.*;

public class ProxyBuilder {
	public static final  String PROXY_SUFFIX  = "PROXY";
	public static final  String ORIGIN_FIELD  = "PROXY_ORIGIN";
	public static final  String WRAPPER_FIELD = "PROXY_WRAPPER";
	private static final Method getReactiveTarget;

	static {
		try {
			getReactiveTarget = ReactiveForwarder.class.getDeclaredMethod("getReactableTarget");
		} catch (NoSuchMethodException e) {
			throw new ProxyException("FATAL: react method not loadable!", e);
		}
	}

	private final InfusionAccessProvider accessor;
	private       int                    counter = 0;

	public ProxyBuilder(InfusionAccessProvider accessProvider) {
		this.accessor = accessProvider;
	}


	public <T> DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends T> buildProxy(
			Class<? extends T> originClass,
			ElementMatcher.Junction<MethodDescription> reactive,
			ElementMatcher.Junction<MethodDescription> unreactive
	) {
		var ignored =
				from(Reactable.class)
						.or(from(ReactiveForwarder.class))
						.or(isEquals())
						.or(isClone());
		var reactTo =
				reactive.and(
						not(
								from(Object.class)
										.or(unreactive)
										.or(ignored)
						)
				);
		return new ByteBuddy()
				.subclass(originClass, ConstructorStrategy.Default.IMITATE_SUPER_CLASS)
				.implement(ReactiveForwarder.class)
				.name(format("%s.%s_%s$%s", accessor.getPackage(originClass), originClass.getSimpleName(), PROXY_SUFFIX, counter++))

				.defineField(ORIGIN_FIELD, originClass, PRIVATE)
				.defineField(WRAPPER_FIELD, ReactiveWrapper.class, PRIVATE)

				.method(from(Reactable.class))
				.intercept(prioritize(ReactiveForwarder.class))

				.method(is(getReactiveTarget))
				.intercept(FieldAccessor.ofField(WRAPPER_FIELD))

				.method(reactTo)
				.intercept(
						MethodDelegation.to(ProxyForwardImpl.ToOrigin.class)
				)

				.method(
						isPublic()
								.and(not(reactTo))
								.and(not(ignored))
				)
				.intercept(
						MethodCall.invokeSelf()
								.onField(ORIGIN_FIELD)
								.withAllArguments()
				)

				.method(isClone())
				.intercept(ExceptionMethod.throwing(CloneNotSupportedException.class))

				.method(isEquals())
				.intercept(
						MethodDelegation.to(ProxyForwardImpl.Equals.class)
				);
	}

}
