package com.niton.reactj.core.observer.infusion;

import com.niton.reactj.api.proxy.InfusionAccessProvider;

import java.lang.invoke.MethodHandles;

public class BesideOriginInfuser implements InfusionAccessProvider {
	private final MethodHandles.Lookup requestingLookup;

	public BesideOriginInfuser(MethodHandles.Lookup requestingLookup) {
		this.requestingLookup = requestingLookup;
	}

	@Override
	public MethodHandles.Lookup getLookup(Class<?> originClass) throws IllegalAccessException {
		return MethodHandles.privateLookupIn(originClass, requestingLookup);
	}

	@Override
	public String getPackage(Class<?> originClass) {
		return originClass.getPackageName();
	}
}
