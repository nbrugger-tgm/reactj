package com.niton.reactj.api.react;


import com.niton.reactj.event.GenericEventManager;

import java.util.Map;

public class ReactiveProxy<T> implements Reactable {

	private final ReactiveWrapper<T> wrapper;
	private final T                  proxy;

	public ReactiveProxy(ReactiveWrapper<T> wrapper, T proxy) {
		this.wrapper = wrapper;
		this.proxy = proxy;
	}

	public T getObject() {
		return proxy;
	}

	@Override
	public Map<String, Object> getState() {
		return wrapper.getState();
	}

	@Override
	public GenericEventManager reactEvent() {
		return wrapper.reactEvent();
	}

	@Override
	public void set(String property, Object value) throws Exception {
		wrapper.set(property, value);
	}

	public void setStrategy(ReactiveStrategy reactOnAll) {
		//TODO: #34
	}
}
