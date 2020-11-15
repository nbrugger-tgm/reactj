package com.niton.reactj;

public class ReactiveProxy<C> {
	public final C                object;
	public final ReactiveModel<C> reactive;

	public ReactiveProxy(C wrapped, ReactiveModel<C> model) {
		this.object = wrapped;
		this.reactive = model;
	}
}
