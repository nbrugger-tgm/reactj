package com.niton.reactj.api.proxy;

import java.lang.invoke.MethodHandles;

/**
 * Used to determine where a proxy should be created and how the access is handled.
 */
public interface InfusionAccessProvider {
    /**
     * Returns a lookup with access to the package returned by {@link #getPackage(Class)}
     *
     * @param originClass the class the proxy will be created for
     *
     * @return the lookup used to infuse the Proxy
     *
     * @throws IllegalAccessException when the executing class can't use the lookup,
     *                                most likely because
     */
    MethodHandles.Lookup getLookup(Class<?> originClass) throws IllegalAccessException;


    /**
     * Returns the package a proxy of the given class should be created in
     *
     * @param originClass the class the proxy will emulate
     *
     * @return the name of the package the proxy should be put into
     */
    String getPackage(Class<?> originClass);
}
