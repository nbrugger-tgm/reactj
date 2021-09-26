package com.niton.reactj.api.proxy.infusion;

import java.lang.invoke.MethodHandles;

public interface InfusionAccessProvider {
	MethodHandles.Lookup getLookup(Class<?> originClass) throws IllegalAccessException;

	String getPackage(Class<?> originClass);
}
