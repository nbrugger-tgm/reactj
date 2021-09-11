package com.niton.reactj.util;

import com.niton.reactj.proxy.ProxySubject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ProxyUtility {
	private ProxyUtility(){}
	private static final String                                      equalsWarning = "[WARNING] 'equals()' calls on ProxySubjects DO NOT use the Object.equals() implementation but `Reactable.getState()` and equals the result. Consider writing a custom equals for \"%s\"";


	/**
	 * Handles equals invocations
	 * @param self the proxy the method was invoked onto
	 * @param thisMethod the called method
	 * @param args the parameters used for invocation
	 * @param backend the backend data
	 * @return the return value of equals, null if no method was called
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static Object handleEquals(Object self, Method thisMethod, Object[] args, Object backend)
			throws IllegalAccessException, InvocationTargetException {
		// only prevent the default Object implementation.
		// If the user overwrote `equals` this should not kick in
		if(thisMethod.getDeclaringClass().equals(Object.class)) {
			System.err.printf(equalsWarning + "%n", self.getClass().getSimpleName());
			return ((ProxySubject) args[0]).getState().equals(((ProxySubject) self).getState());
		}
		//if call is not "mocked" by the proxy, just forward to the actual object
		if(thisMethod.getDeclaringClass().equals(backend.getClass())) {
			// System.err.println("[WARNING] "+backend.getClass().getTypeName()+".equals()
			// implementation should also support subclasses of "+backend.getClass().getTypeName());
			return thisMethod.invoke(backend, args);
		}
		return null;
	}
}
