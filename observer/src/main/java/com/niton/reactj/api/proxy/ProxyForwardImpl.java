package com.niton.reactj.api.proxy;

import com.niton.reactj.api.exceptions.ReactiveAccessException;
import com.niton.reactj.api.exceptions.ReactiveException;
import com.niton.reactj.api.react.ReactiveWrapper;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.FieldValue;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.niton.reactj.api.proxy.ProxyBuilder.*;

public class ProxyForwardImpl {
	public static class ToOrigin {
		private ToOrigin() {
		}

		@RuntimeType
		public static Object forward(
				@Origin Method target,
				@FieldValue(WRAPPER_FIELD) ReactiveWrapper<?> reactable,
				@FieldValue(ORIGIN_FIELD) Object origin,
				@AllArguments Object[] args
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

	public static class Equals {
		private Equals() {
		}

		@RuntimeType
		public static Object equals(
				@FieldValue(ORIGIN_FIELD) Object origin,
				@AllArguments Object[] arg
		) {
			return arg[0].equals(origin);
		}
	}

	private ProxyForwardImpl() {
	}
}
