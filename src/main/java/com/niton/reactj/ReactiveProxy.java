package com.niton.reactj;

import com.niton.reactj.mvc.GenericEventManager;

import java.util.Map;

public class ReactiveProxy<T> implements Reactable{

	private final ReactiveWrapper<T> reactor;
	private final T proxy;

	public ReactiveProxy(ReactiveWrapper<T> reactor, T proxy) {
		this.reactor = reactor;
		this.proxy = proxy;
	}

	public T getObject() {
		return proxy;
	}

	@Override
	public Map<String, Object> getState() {
		return reactor.getState();
	}

	@Override
	public GenericEventManager reactEvent() {
		return reactor.reactEvent();
	}

	@Override
	public void set(String property, Object value) throws Exception {
		reactor.set(property,value);
	}

	public void setStrategy(ReactiveStrategy reactOnAll) {
		//TODO: #34
	}
}
