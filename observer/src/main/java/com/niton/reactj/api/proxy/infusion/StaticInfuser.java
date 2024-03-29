package com.niton.reactj.api.proxy.infusion;

import java.lang.invoke.MethodHandles;

/**
 * Creates Proxies besides a predefined class using a self-owned lookup.
 * <p>
 * The difference to {@link StaticInfuserWithLookup} is that here a private lookup to the {@link #anchor} is created
 * while {@link StaticInfuserWithLookup} is instantiated ith a private lookup already
 */
public class StaticInfuser implements InfusionAccessProvider {
	private final Class<?>             anchor;
	private final MethodHandles.Lookup requestingLookup;

	public StaticInfuser(Class<?> anchor, MethodHandles.Lookup requestingLookup) {
		this.anchor           = anchor;
		this.requestingLookup = requestingLookup;
	}

	@Override
	public MethodHandles.Lookup getLookup(Class<?> originClass) throws IllegalAccessException {
		return MethodHandles.privateLookupIn(anchor, requestingLookup);
	}

	@Override
	public String getPackage(Class<?> originClass) {
		return anchor.getPackageName();
	}
}
