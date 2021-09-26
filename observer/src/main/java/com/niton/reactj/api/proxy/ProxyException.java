package com.niton.reactj.api.proxy;

import com.niton.reactj.api.exceptions.ReactiveException;

public class ProxyException extends RuntimeException {
	public ProxyException(String message) {
		super(message);
	}

	public ProxyException(String message, Throwable cause) {
		super(message, cause);
	}


	public static ProxyException doubleProxyException(Class<?> originClass) {
		return new ProxyException(
				"You can't create a proxy from a proxy",
				new IllegalArgumentException(originClass.getName())
		);
	}

	public static void publicFieldException(Class<?> originClass) {
		throw new ReactiveException(
				"Class " + originClass.getName() +
						" contains public writable instance variables, such classes can't be proxied\n" +
						"If possible encapsulate using getters & setters, if not use ProxyCreator.allowUnsafeProxies\n" +
						"Be aware that if you use unsafe proxies you need to sync using ProxyCreator.sync()"
		);
	}

	public static ProxyException constructorAccessException(Class<?> proxyClass) {
		return new ProxyException("Couldn't access parameterless constructor of " + proxyClass.getSuperclass().getName());
	}

	public static ProxyException noParameterlessConstructor(Class<?> proxyClass) {
		return new ProxyException("Unable to find parameterless constructor of " + proxyClass.getSuperclass().getName());
	}
}
