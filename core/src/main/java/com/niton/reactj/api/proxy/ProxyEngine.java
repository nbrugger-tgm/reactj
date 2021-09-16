package com.niton.reactj.api.proxy;


import javassist.util.proxy.MethodHandler;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.niton.reactj.api.util.ReflectiveUtil.executeCall;

/**
 * The baseclass for proxies
 *
 * @param <T> the type the proxy emulates
 */
public abstract class ProxyEngine<T> implements MethodHandler, Serializable {
	private static final String equalsWarning = "'%s' has no `equals()` implementation!";

	private final T original;

	protected ProxyEngine(T real) {
		original = real;
	}

	protected T getOriginal() {
		return original;
	}


	@Override
	public Object invoke(Object self, Method method, Method proxyMethod, Object[] args)
			throws InvocationTargetException, IllegalAccessException {
		method.setAccessible(true);

		Object result;

		if (method.getName().equals("equals")) {
			Object o = handleEquals(self, method, args);
			if (o != null) {
				return o;
			}
		}

		if (method.getName().equals("hashCode"))
			return method.invoke(original, args);


		if (useCustomImplementation(method, args))
			result = executeImplementation(method, args);
		else {
			result = executeCall(original, method, args);
			postExecution(method, args);
		}

		return result;
	}

	/**
	 * Handles equals invocations
	 *
	 * @param self       the proxy the method was invoked onto
	 * @param thisMethod the called method
	 * @param args       the parameters used for invocation
	 *
	 * @return the return value of equals, null if no method was called
	 */
	public Object handleEquals(Object self, Method thisMethod, Object[] args)
			throws IllegalAccessException, InvocationTargetException {
		// only prevent the default Object implementation.
		// If the user overwrote `equals` this should not kick in
		if (thisMethod.getDeclaringClass().equals(Object.class)) {
			System.err.printf(equalsWarning + "%n", self.getClass().getSimpleName());
			return ((ProxySubject) args[0]).getState().equals(((ProxySubject) self).getState());
		}
		//if call is not "mocked" by the proxy, just forward to the actual object
		if (thisMethod.getDeclaringClass().equals(original.getClass())) {
			// System.err.println("[WARNING] "+backend.getClass().getTypeName()+".equals()
			// implementation should also support subclasses of "+backend.getClass().getTypeName());
			return thisMethod.invoke(original, args);
		}
		return null;
	}

	protected abstract boolean useCustomImplementation(Method thisMethod, Object[] args);

	protected abstract Object executeImplementation(Method thisMethod, Object[] args)
			throws InvocationTargetException, IllegalAccessException;

	protected abstract void postExecution(Method proceed, Object[] args);
}
