package com.niton.reactj.api.react;

import com.niton.reactj.api.annotation.Unreactive;
import com.niton.reactj.api.proxy.ProxyCreator;
import com.niton.reactj.api.proxy.ProxyEngine;
import com.niton.reactj.api.proxy.ProxySubject;
import com.niton.reactj.api.util.ReflectiveUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.niton.reactj.api.react.ReactiveStrategy.REACT_ON_SETTER;

/**
 * A proxy providing automatic reacting to method calls.
 * <p>
 * This is used to make objects reactive that are not reactive by default and therefore be able
 * to use them in ReactiveComponents
 *
 * @param <M> The type this Model is going to wrap
 */
public final class ReactiveProxyEngine<M> extends ProxyEngine<M> {
	private final static Map<Method,Method> subjectMethodMap = new HashMap<>();

	private final ReactiveWrapper<M> wrapper  = new ReactiveWrapper<>(getOriginal());
	@Unreactive
	private       ReactiveStrategy   strategy = REACT_ON_SETTER;
	@Unreactive
	private       String[]           reactToList;

	/**
	 * Creates a proxy forwarding Reactive calls to 'real'
	 *
	 * @param real the real model behind. will be used as storage and not be directly accessible anymore
	 */
	public ReactiveProxyEngine(M real) {
		super(real);
	}

	@Override
	protected boolean useCustomImplementation(Method method, Object[] args) {
		return isDefined(method, ProxySubject.class) ||
		       isDefined(method, Reactable.class);
	}
	public boolean isDefined(Method method, Class<?> type){
		return method.getDeclaringClass().equals(type);
	}
	@Override
	protected Object executeImplementation(Method thisMethod, Object[] args)
			throws InvocationTargetException, IllegalAccessException {

		if(!subjectMethodMap.containsKey(thisMethod))
			subjectMethodMap.put(thisMethod, ReflectiveUtil.getOriginMethod(thisMethod, Reactable.class));

		Method originMethod = subjectMethodMap.get(thisMethod);

		return originMethod.invoke(wrapper, args);
	}

	@Override
	protected void postExecution(Method proceed, Object[] args) {
		if (strategy.reactTo(proceed.getName(), reactToList))
			wrapper.react();
	}

	public ReactiveWrapper<M> getWrapper() {
		return wrapper;
	}

	public ReactiveStrategy getStrategy() {
		return strategy;
	}

	/**
	 * Set the strategy on how to react to method changes (only has an effect on proxies created by {@link
	 * ProxyCreator#subject(Class, Object...)})
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

}
