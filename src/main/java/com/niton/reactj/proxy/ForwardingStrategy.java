package com.niton.reactj.proxy;

import java.lang.reflect.Method;

@FunctionalInterface
public interface ForwardingStrategy {
	/**
	 * Checks if the method call should be forwarded to a certain target
	 *
	 * @param calledUpon the proxy the method was called on
	 * @param method     the called method (Type$Proxy.method not Type.method)
	 * @param args       the args used to call the variable
	 *
	 * @return true if the method should be forwarded
	 */
	boolean checkForward(Object calledUpon, Method method, Object[] args);
}
