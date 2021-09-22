package com.niton.reactj.api.proxy;

import com.niton.reactj.api.exceptions.ReactiveException;
import com.niton.reactj.api.react.Reactable;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.FieldValue;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ProxyForwardImpl {
	private ProxyForwardImpl() {
	}

	public static class ToOrigin {
		@RuntimeType
		public static Object forward(
				@Origin
						Method target,
				@FieldValue(ProxyCreator.wrapperField)
						Reactable reactable,
				@FieldValue(ProxyCreator.originField) Object origin,
				@AllArguments
						Object[] args
		) throws InvocationTargetException {
			try {
				try {
					return target.invoke(origin, args);
				} catch (IllegalArgumentException e) {
					throw new ReactiveException("Couldn't forward arguments!", e);
				} catch (IllegalAccessException accessException) {
					if (accessException.getMessage().contains("module"))
						throw new ReactiveException("Proxy method couldn't be accessed (make sure you open your module ot reactj.core)", accessException);
					throw new ReactiveException("Proxy method couldn't be accessed", accessException);
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
				@FieldValue(ProxyCreator.originField)
						Object origin,
				@AllArguments
						Object[] arg) {
			return arg[0].equals(origin);
		}
	}
}
