package com.niton.reactj.proxy;

import com.niton.reactj.Reactable;
import com.niton.reactj.ReactiveObject;
import com.niton.reactj.ReactiveStrategy;
import com.niton.reactj.annotation.Unreactive;
import com.niton.reactj.exceptions.ReactiveException;
import com.niton.reactj.mvc.GenericEventManager;
import com.niton.reactj.util.ProxyUtility;
import com.niton.reactj.util.ReactiveReflectorUtil;
import javassist.util.proxy.MethodHandler;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.niton.reactj.ReactiveStrategy.REACT_ON_SETTER;

/**
 * A proxy providing automatic reacting to method calls.
 * <p>
 * This is used to make objects reactive that are not reactive by default and therefore be able
 * to use them in ReactiveComponents
 *
 * @param <M> The type this Model is going to wrap
 */
public final class ReactiveProxy<M> implements MethodHandler, Reactable, Serializable {
	@Unreactive
	private final        M                                           backend;
	@Unreactive
	private              M                       proxy;
	@Unreactive
	private              ReactiveStrategy        strategy      = REACT_ON_SETTER;
	@Unreactive
	private              String[]                reactToList;

	/**
	 * Creates a proxy forwarding Reactive calls to 'real'
	 *
	 * @param real the real model behind. will be used as storage and not be directly accessible anymore
	 */
	public ReactiveProxy(M real) {
		backend = real;
	}

	/**
	 * Create a new ReactiveProxy from a certain class.
	 * <p>
	 * A Proxy manages reactivity automatically. So no need to extend ReactiveObject.<br>
	 * This function uses the constructor of the given type. So the type <b>MUST</b> have an accessible constructor. The constructor is allowed to have arguments
	 *
	 * @param type            The type the ReactiveProxy should emulate (eg. Person.class)
	 * @param constructorArgs the arguments to pass to the constructor
	 * @param <M>             the type the Proxy will emulate
	 * @return the created proxy
	 */
	public static <M> ReactiveProxy<M> createProxy(Class<M> type, Object... constructorArgs) {
		return ReactiveObject.createProxy(type, constructorArgs);
	}

	/**
	 * Works similar ro {@link #createProxy(Class, Object...)} but takes advantage over "Pseudo Proxies" (https://github.com/nbrugger-tgm/reactj/issues/31)<br>
	 * Described more detailed here : https://github.com/nbrugger-tgm/reactj/wiki/Models#proxysubject
	 *
	 * @param type              the class to create a proxy for
	 * @param constructorParams constructor parameters used to build the object
	 * @param <C>               the type of the object to create a proxy for
	 * @return an instance of {@code <C>} but within a proxy
	 * @throws ReactiveException if stuff goes wrong, many things can cause this. Mostly reflective missfunction
	 */
	public static <C extends ProxySubject> C create(Class<C> type, Object... constructorParams)
	throws
	ReactiveException {
		return ReactiveObject.create(type, constructorParams);
	}

	/**
	 * Creates a proxy similar to {@link ReactiveProxy#create(Class, Object...)} but from a "live" object
	 *
	 * @param original the object to create the proxy for
	 * @return the wrapped object
	 */
	public static <C extends ProxySubject> C wrap(C original, Object... constructorParams) {
		return ReactiveObject.wrap(original, constructorParams);
	}

	/**
	 * Creates a proxy similar to {@link ReactiveProxy#createProxy(Class, Object...)} but from a "live object instead of creating a new one
	 *
	 * @param original          the object to wrap with the proxy
	 * @param constructorParams the parameters for the construction of the proxy. (must match a constructor from {@code <C>}
	 * @param <C>               the type to create the proxy for
	 * @return a reactive proxy covering the original object
	 */
	public static <C> ReactiveProxy<C> wrap(C original, Object... constructorParams) {
		return ReactiveObject.wrap(original, constructorParams);
	}

	/**
	 * This returns the mutable Objects.
	 * <p>
	 * <br/><b>Calls to this object will be reacted to!</b>
	 *
	 * @return the reactive object
	 */
	public M getObject() {
		return proxy;
	}

	public void setProxy(M proxy) {
		if(this.proxy != null)
			throw new UnsupportedOperationException("The proxy can't be changed after creation");
		this.proxy = proxy;
	}

	public ReactiveStrategy getStrategy() {
		return strategy;
	}

	/**
	 * Set the strategy on how to react to method changes (only has an effect on proxies created by {@link #create(Class, Object...)})
	 *
	 * @param strategy the strategy to use
	 */
	public void setStrategy(ReactiveStrategy strategy) {
		this.strategy = strategy;
	}

	public List<String> getReactToList() {
		return Arrays.asList(reactToList);
	}

	/**
	 * Set the methods names to react to when using {@link ReactiveStrategy#REACT_ON_CUSTOM}
	 *
	 * @param reactTo a list of method names to react to
	 */
	public void reactTo(String... reactTo) {
		reactToList = reactTo;
	}

	@Override
	public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args)
	throws InvocationTargetException, IllegalAccessException {
		thisMethod.setAccessible(true);
		//When equals is called with ProxySubject as parameter and called on a subject proxy -> ps.equals(otherPs)
		if(
			thisMethod.getName().equals("equals") &&
				args[0] instanceof ProxySubject &&
				self instanceof ProxySubject
		) {
			Object o = ProxyUtility.handleEquals(self, thisMethod, args, backend);
			if(o != null) {
				return o;
			}
		}
		//When methods originates from Proxy Subject
		if(originatesFromPSubject(thisMethod, self)) {
			return forwardPSubjectCallToMyself(thisMethod, args);
		}
		Object returnValue = thisMethod.invoke(backend, args);

		if(strategy.reactTo(thisMethod.getName(), reactToList)) {
			react();
		}

		return returnValue;
	}



	private boolean originatesFromPSubject(Method thisMethod, Object self) {
		return thisMethod.getDeclaringClass().equals(ProxySubject.class)
			|| (thisMethod.getDeclaringClass().equals(Reactable.class)
			&& self instanceof ProxySubject
		);
	}

	private Object forwardPSubjectCallToMyself(Method thisMethod, Object[] args)
	throws InvocationTargetException, IllegalAccessException {
		try {
			return ReactiveProxy.class.getMethod(thisMethod.getName(),
			                                     thisMethod.getParameterTypes())
			                          .invoke(this, args);
		} catch(NoSuchMethodException e) {
			//No way this happens
			throw new ReactiveException("unexpected failure", e);
		}
	}

	@Override
	public Map<String, Object> getState() {
		return ReactiveReflectorUtil.getState(backend);
	}

	@Override
	public void set(String property, Object value) throws Exception {
		ReactiveReflectorUtil.updateField(backend, property, value);
	}
	private final GenericEventManager reactEvent = new GenericEventManager();

	@Override
	public GenericEventManager reactEvent() {
		return reactEvent;
	}

	//private EventManager<PropertyObservation> changeEvent = new EventManager<>();
	//@Override
	//public EventManager<PropertyObservation> getEventManager() {
	//	return changeEvent;
	//}
}
