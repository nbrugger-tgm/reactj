package com.niton.reactj;

import java.util.Map;

public class ReactiveProxy<C> implements Reactable {
	public final C                object;
	public final ReactiveModel<C> reactive;

	public ReactiveProxy(C wrapped, ReactiveModel<C> model) {
		this.object = wrapped;
		this.reactive = model;
	}

	@Override
	public void bind(ReactiveController<?> c) {
		reactive.bind(c);
	}

	@Override
	public Map<String, Object> getState() {
		return reactive.getState();
	}

	@Override
	public void unbind(ReactiveController<?> c) {
		reactive.unbind(c);
	}

	@Override
	public void react() {
		reactive.react();
	}

	@Override
	public void set(String property, Object value) throws Throwable {
		reactive.set(property, value);
	}
}
