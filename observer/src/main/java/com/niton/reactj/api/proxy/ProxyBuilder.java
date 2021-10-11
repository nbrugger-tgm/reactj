package com.niton.reactj.api.proxy;

import com.niton.reactj.api.observer.Reactable;
import com.niton.reactj.api.proxy.infusion.InfusionAccessProvider;
import com.niton.reactj.api.react.ReactiveForwarder;
import com.niton.reactj.api.react.ReactiveWrapper;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition;
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

/**
 * Used to construct raw proxy templates that are {@link Reactable}
 * <
 */
public class ProxyBuilder {
	public static final  String PROXY_SUFFIX  = "PROXY";
	public static final  String ORIGIN_FIELD  = "PROXY_ORIGIN";
	public static final  String WRAPPER_FIELD = "PROXY_WRAPPER";
	private static final Method getReactiveTarget;
	private static int                    counter = 0;

	static {
		try {
			getReactiveTarget = ReactiveForwarder.class.getDeclaredMethod("getReactableTarget");
		} catch (NoSuchMethodException e) {
			throw new ProxyException("FATAL: react method not loadable!", e);
		}
	}

	private final  InfusionAccessProvider accessor;

	public ProxyBuilder(InfusionAccessProvider accessProvider) {
		this.accessor = accessProvider;
	}


	/**
	 * Constructs a base proxy class that provides an {@link Reactable} implementation.
	 * You can further modify the proxy class
	 * <p>
	 * The proxy can handle {@link Object#equals(Object)},  {@link Object#clone()} and {@link Object#hashCode()}.
	 * The proxy DOES NOT SUPPORT CLONING.
	 * </p>
	 * <p>
	 * The proxy sends reactable calls to {@link Reactable#react()}. Also the handling of java intern methods is
	 * already done. Calls {@link Object} methods will never be reacted to, even if they ´change the objects state
	 * because the should never do so.
	 * </p>
	 * <p>
	 * This proxy is not complete and meant to be extended and built. To build use {@link ReceiverTypeDefinition#make()}
	 * and {@link DynamicType.Unloaded#load(ClassLoader)}
	 * </p>
	 *
	 * @param originClass the class to create the proxy for
	 * @param reactive    a descriptor which methods to react to. Elements matched by this filter will NOT be matched if
	 *                    they are matched by the exclusion filter (unreactive parameter)
	 * @param unreactive  a descriptor which methods <b>not</b> to react to. Elements matched by this filter will
	 *                    never be reacted to.
	 * @param <T>         the type the proxy will emulate
	 *
	 * @return the template for the proxy class. Can be extended using {@link ByteBuddy}
	 */
	public <T> ReceiverTypeDefinition<T> buildProxy(
			Class<T> originClass,
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
				.name(format(
						"%s.%s_%s$%s",
						accessor.getPackage(originClass),
						originClass.getSimpleName(),
						PROXY_SUFFIX,
						counter++
				))

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
				)
				;
	}

}
