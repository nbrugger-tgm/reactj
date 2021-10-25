package com.niton.reactj.test.proxy;

import com.niton.reactj.api.proxy.InfusionAccessProvider;
import com.niton.reactj.api.proxy.ProxyBuilder;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition;
import net.bytebuddy.matcher.ElementMatcher.Junction;

public class MockProxyBuilder implements ProxyBuilder {
	@Override
	public <T> ReceiverTypeDefinition<T> buildProxy(
			Class<T> originClass, Junction<MethodDescription> reactive, Junction<MethodDescription> unreactive
	) {
		return null;
	}

	@Override
	public void useInfusion(InfusionAccessProvider accessor) {
		//NOPE
	}
}
