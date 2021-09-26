package com.niton.reactj.api.proxy.infusion;

import java.lang.invoke.MethodHandles;

public class StaticInfuserWithLookup implements InfusionAccessProvider {

	private final MethodHandles.Lookup lookup;
	private final Class<?>             anchor;

	public StaticInfuserWithLookup(Class<?> anchor, MethodHandles.Lookup lookup) {
		this.anchor = anchor;
		this.lookup = lookup;
	}

	@Override
	public MethodHandles.Lookup getLookup(Class<?> originClass) {
		return lookup;
	}

	@Override
	public String getPackage(Class<?> originClass) {
		return anchor.getPackageName();
	}
}
