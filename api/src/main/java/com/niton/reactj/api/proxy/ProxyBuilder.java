package com.niton.reactj.api.proxy;

import com.niton.reactj.api.react.ReactiveWrapper;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.ServiceLoader;

public interface ProxyBuilder {
	/**
	 * This suffix is appended to proxy names
	 */
	String PROXY_SUFFIX     = "PROXY";
	/**
	 * This is the name of the field within a proxy the origin object
	 * is stored in. Can be used for reflective access
	 */
	String ORIGIN_FIELD     = "PROXY_ORIGIN";
	/**
	 * This is the name of the field within a proxy the {@link ReactiveWrapper}
	 * is stored in.
	 */
	String WRAPPER_FIELD    = "PROXY_WRAPPER";
	/**
	 * A regex that matches any name of a proxy class
	 */
	String PROXY_NAME_REGEX = ".+_" + PROXY_SUFFIX + "\\$[0-9]+";

	static ProxyBuilder load(InfusionAccessProvider accessor) {
		var builder = ServiceLoader.load(ProxyBuilder.class)
		                           .findFirst()
		                           .orElseThrow(ProxyBuilder::noProxyBuilderException);
		builder.useInfusion(accessor);
		return builder;
	}

	private static RuntimeException noProxyBuilderException() {
		return new RuntimeException(
				new ClassNotFoundException("No AbstractProxyBuilder implementation found. Use JPMS to provide an impl.")
		);
	}

	void useInfusion(InfusionAccessProvider accessor);

	<T> ReceiverTypeDefinition<T> buildProxy(
			Class<T> originClass,
			ElementMatcher.Junction<MethodDescription> reactive,
			ElementMatcher.Junction<MethodDescription> unreactive
	);
}
