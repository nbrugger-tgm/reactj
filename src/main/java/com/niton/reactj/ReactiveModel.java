package com.niton.reactj;

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
 * @param <M>
 */
public class ReactiveModel<M> implements MethodHandler, Reactable {
	protected final List<ReactiveController<?>> listeners = new ArrayList<>();
	final   M                model;
	private ReactiveStrategy strategy = REACT_ON_SETTER;
	private String[]         reactTo;

	public ReactiveModel(M model) {
		this.model = model;
	}

	public ReactiveStrategy getStrategy() {
		return strategy;
	}

	public M getData() {
		return model;
	}

	public void setStrategy(ReactiveStrategy strategy) {
		this.strategy = strategy;
	}

	public String[] getReactTo() {
		return reactTo;
	}

	public void reactTo(String... reactTo) {
		this.reactTo = reactTo;
	}

	@Override
	public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws InvocationTargetException, IllegalAccessException {
		thisMethod.setAccessible(true);
		Object ret = thisMethod.invoke(model, args);
		boolean react = strategy.reactTo(thisMethod.getName(), reactTo);
		if (react)
			react();
		return ret;
	}

	@Override
	public void bind(ReactiveController<?> c) {
		listeners.add(c);
	}

	@Override
	public Map<String, Object> getState() {
		return ReactiveReflectorUtil.getState(model);
	}

	@Override
	public void unbind(ReactiveController<?> c) {
		listeners.remove(c);
	}

	public void react() {
		listeners.forEach(ReactiveController::modelChanged);
	}

	@Override
	public void react(String property, Object value) {
		listeners.forEach(l -> l.modelChanged(Collections.singletonMap(property,value)));
	}

	@Override
	public void set(String property, Object value) throws Throwable {
		ReactiveReflectorUtil.updateField(model, property, value);
	}
}
