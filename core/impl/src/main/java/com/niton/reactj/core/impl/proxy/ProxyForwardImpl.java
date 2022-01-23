package com.niton.reactj.core.impl.proxy;

import com.niton.reactj.api.exceptions.ReactiveAccessException;
import com.niton.reactj.api.exceptions.ReactiveException;
import com.niton.reactj.api.react.Reactable;
import com.niton.reactj.api.react.ReactiveWrapper;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.FieldValue;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.niton.reactj.api.proxy.ProxyBuilder.*;


/**
 * These are the method implementations used for {@link com.niton.reactj.api.react.Reactable}
 * proxies
 */
public class ProxyForwardImpl {
	/**
	 * Forwards calls to the origin of the proxy and calls {@link Reactable#react()} afterwards
	 */
	public static class ToOrigin {
		private ToOrigin() {
		}

		@RuntimeType
		public static Object forward(
				@Origin
						Method target,
				@FieldValue(WRAPPER_FIELD)
						ReactiveWrapper<?> reactable,
				@FieldValue(ORIGIN_FIELD)
						Object origin,
				@AllArguments
						Object[] args
		) throws InvocationTargetException {
			try {
				try {
					return target.invoke(origin, args);
				} catch (IllegalArgumentException e) {
					throw new ReactiveException("Couldn't forward arguments!", e);
				} catch (IllegalAccessException accessException) {
					throw new ReactiveAccessException(accessException);
				}
			} finally {
				reactable.react();
			}
		}
	}

	/**
	 * Implementation of {@link Object#equals(Object)} that uses the origin of the Proxy for
	 * comparison
	 */
	public static class Equals {
		private Equals() {
		}

		@RuntimeType
		public static Object equals(
				@FieldValue(ORIGIN_FIELD)
						Object origin,
				@AllArguments
						Object[] arg
		) {
			return arg[0].equals(origin);
		}
	}

	private ProxyForwardImpl() {
	}
}
