package com.niton.reactj.mvc;

import com.niton.reactj.Observer;
import com.niton.reactj.Reactable;
import com.niton.reactj.ReactiveReflectorUtil;
import com.niton.reactj.ReactiveStrategy;
import javassist.util.proxy.MethodHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.niton.reactj.ReactiveStrategy.REACT_ON_SETTER;

/**
 * Only for internal use
 *
 * @param <M> The type this Model is going to wrap
 */
public class ReactiveModel<M> implements MethodHandler, Reactable {
	protected final List<Observer<?>> listeners = new ArrayList<>();
	final           M                 model;
	private         ReactiveStrategy  strategy  = REACT_ON_SETTER;
	private         String[]          reactTo;

	public ReactiveModel(M model) {
		this.model = model;
	}

	public ReactiveStrategy getStrategy() {
		return strategy;
	}

	public void setStrategy(ReactiveStrategy strategy) {
		this.strategy = strategy;
	}

	public M getData() {
		return model;
	}

	public String[] getReactTo() {
		return reactTo;
	}

	public void reactTo(String... reactTo) {
		this.reactTo = reactTo;
	}

	@Override
	public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args)
	throws InvocationTargetException, IllegalAccessException {
		thisMethod.setAccessible(true);
		Object  ret   = thisMethod.invoke(model, args);
		boolean react = strategy.reactTo(thisMethod.getName(), reactTo);
		if (react) {
			react();
		}
		return ret;
	}

	@Override
	public void bind(Observer<?> observer) {
		listeners.add(observer);
	}

	@Override
	public Map<String, Object> getState() {
		return ReactiveReflectorUtil.getState(model);
	}

	@Override
	public void unbind(Observer<?> observer) {
		listeners.remove(observer);
	}

	public void react() {
		listeners.forEach(Observer::update);
	}

	@Override
	public void react(String property, Object value) {
		listeners.forEach(l -> l.update(Collections.singletonMap(property, value)));
	}

	@Override
	public void set(String property, Object value) throws Throwable {
		ReactiveReflectorUtil.updateField(model, property, value);
	}
}
