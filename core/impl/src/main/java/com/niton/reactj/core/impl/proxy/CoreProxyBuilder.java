package com.niton.reactj.core.impl.proxy;


import com.niton.reactj.api.proxy.InfusionAccessProvider;
import com.niton.reactj.api.proxy.ProxyBuilder;
import com.niton.reactj.api.proxy.ProxyException;
import com.niton.reactj.api.react.Reactable;
import com.niton.reactj.api.react.ReactiveForwarder;
import com.niton.reactj.api.react.ReactiveWrapper;
import com.niton.reactj.api.util.Matchers;
import com.niton.reactj.core.impl.proxy.ProxyForwardImpl.Equals;
import com.niton.reactj.core.impl.proxy.ProxyForwardImpl.ToOrigin;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.*;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;

import static java.lang.String.format;
import static java.lang.reflect.Modifier.PRIVATE;
import static net.bytebuddy.matcher.ElementMatchers.not;

/**
 * Used to construct raw proxy templates that are {@link Reactable}
 */
public class CoreProxyBuilder implements ProxyBuilder {
	private static final Method getReactiveTarget;
	private static       int    counter = 0;

	static {
		try {
			var reactiveForwarder = ReactiveForwarder.class;
			getReactiveTarget = reactiveForwarder.getDeclaredMethod("getReactableTarget");
		} catch (NoSuchMethodException e) {
			throw new ProxyException("FATAL: react method not loadable!", e);
		}
	}

	private InfusionAccessProvider accessor;

	@Override
	public void useInfusion(InfusionAccessProvider accessor) {
		this.accessor = accessor;
	}

	/**
	 * Constructs a base proxy class that provides an {@link Reactable} implementation.
	 * You can further modify the proxy class
	 * <p>
	 * The proxy can handle : <br/>
	 * {@link Object#equals(Object)},  {@link Object#clone()} and {@link Object#hashCode()}.<br/>
	 * The proxy DOES NOT SUPPORT CLONING.
	 * </p>
	 * <p>
	 * The proxy sends reactable calls to {@link Reactable#react()}.
	 * Also the handling of java intern methods is already done.
	 * Calls {@link Object} methods will never be reacted to,
	 * even if they change the objects state because the should never do so.
	 * </p>
	 * <p>
	 * This proxy is not complete and meant to be extended and built. To build use {@link
	 * ReceiverTypeDefinition#make()}
	 * and {@link DynamicType.Unloaded#load(ClassLoader)}
	 * </p>
	 *
	 * @param originClass the class to create the proxy for
	 * @param reactive    a descriptor which methods to react to. Elements matched by this filter
	 *                    will NOT be matched if
	 *                    they are matched by the exclusion filter (unreactive parameter)
	 * @param unreactive  a descriptor which methods <b>not</b> to react to. Elements matched by
	 *                    this filter will
	 *                    never be reacted to.
	 * @param <T>         the type the proxy will emulate
	 *
	 * @return the template for the proxy class. Can be extended using {@link ByteBuddy}
	 */
	@Override
	public <T> ReceiverTypeDefinition<T> buildProxy(
			Class<T> originClass,
			ElementMatcher.Junction<MethodDescription> reactive,
			ElementMatcher.Junction<MethodDescription> unreactive
	) {
		var ignored = Matchers.from(Reactable.class)
		                      .or(Matchers.from(ReactiveForwarder.class))
		                      .or(ElementMatchers.isEquals())
		                      .or(ElementMatchers.isClone());
		var reactTo = reactive.and(
				not(
						Matchers.from(Object.class)
						        .or(unreactive)
						        .or(ignored)
				)
		);
		var proxy = new ByteBuddy()
				.subclass(originClass, ConstructorStrategy.Default.IMITATE_SUPER_CLASS)
				.implement(ReactiveForwarder.class)
				.name(getNextQualifiedProxyName(originClass));

		proxy = defineFields(proxy, originClass);
		return defineMethodInterceptors(proxy, reactTo, ignored);
	}

	private <T> String getNextQualifiedProxyName(Class<T> originClass) {
		return format(
				"%s.%s_%s$%s",
				accessor.getPackage(originClass),
				originClass.getSimpleName(),
				PROXY_SUFFIX,
				nextProxyId()
		);
	}

	private <T> DynamicType.Builder<T> defineFields(
			DynamicType.Builder<T> proxy,
			Class<T> originClass
	) {
		return proxy.defineField(ORIGIN_FIELD, originClass, PRIVATE)
		            .defineField(WRAPPER_FIELD, ReactiveWrapper.class, PRIVATE);
	}

	private <T> ReceiverTypeDefinition<T> defineMethodInterceptors(
			DynamicType.Builder<T> proxy,
			ElementMatcher.Junction<MethodDescription> reactTo,
			ElementMatcher<? super MethodDescription> ignored
	) {
		return proxy.method(Matchers.from(Reactable.class))
		            .intercept(DefaultMethodCall.prioritize(ReactiveForwarder.class))

		            .method(ElementMatchers.is(getReactiveTarget))
		            .intercept(FieldAccessor.ofField(WRAPPER_FIELD))

		            .method(reactTo)
		            .intercept(MethodDelegation.to(ToOrigin.class))

		            .method(
				            ElementMatchers.isPublic()
				                           .and(not(reactTo))
				                           .and(not(ignored))
		            )
		            .intercept(
				            MethodCall.invokeSelf()
				                      .onField(ORIGIN_FIELD)
				                      .withAllArguments()
		            )

		            .method(ElementMatchers.isClone())
		            .intercept(ExceptionMethod.throwing(CloneNotSupportedException.class))

		            .method(ElementMatchers.isEquals())
		            .intercept(MethodDelegation.to(Equals.class));
	}

	private static int nextProxyId() {
		return counter++;
	}

}
