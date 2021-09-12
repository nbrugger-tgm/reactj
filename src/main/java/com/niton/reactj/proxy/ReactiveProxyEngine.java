package com.niton.reactj.proxy;

import com.niton.reactj.Reactable;
import com.niton.reactj.ReactiveStrategy;
import com.niton.reactj.ReactiveWrapper;
import com.niton.reactj.annotation.Unreactive;
import com.niton.reactj.exceptions.ReactiveException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static com.niton.reactj.ReactiveStrategy.REACT_ON_SETTER;
import static com.niton.reactj.util.ReflectiveUtil.getMethodSignature;
import static java.lang.String.format;

/**
 * A proxy providing automatic reacting to method calls.
 * <p>
 * This is used to make objects reactive that are not reactive by default and therefore be able
 * to use them in ReactiveComponents
 *
 * @param <M> The type this Model is going to wrap
 */
public final class ReactiveProxyEngine<M> extends ProxyEngine<M> {
	private final ReactiveWrapper<M> wrapper  = new ReactiveWrapper<>(getBackend());
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
	protected boolean useCustomImplementation(Method thisMethod, Object[] args) {
		return thisMethod.getDeclaringClass().equals(ProxySubject.class);
	}

	@Override
	protected Object executeImplementation(Method thisMethod, Object[] args)
			throws InvocationTargetException, IllegalAccessException {
		if (!thisMethod.getDeclaringClass().equals(Reactable.class))
			throw new ReactiveException(format("%s is not implemented in %s",
			                                   getMethodSignature(thisMethod),
			                                   getBackend().getClass()
			));
		return thisMethod.invoke(wrapper, args);
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
