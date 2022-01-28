package com.niton.reactj.api.proxy.infusion;

import com.niton.reactj.api.proxy.InfusionAccessProvider;

import java.lang.invoke.MethodHandles;

/**
 * This class is used to infuse a proxy into the same package as the class it proxies.
 * <p>
 * Requires a {@link MethodHandles#privateLookupIn(Class, MethodHandles.Lookup)}
 * so the module providing this class needs private reflection access to the base class of the
 * proxy.
 * </p>
 */
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
