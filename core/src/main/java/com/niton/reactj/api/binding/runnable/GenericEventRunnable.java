package com.niton.reactj.api.binding.runnable;

import com.niton.reactj.utils.event.GenericEventEmitter;

public class GenericEventRunnable {
	private final Runnable consumer;

	public GenericEventRunnable(Runnable consumer, GenericEventEmitter emitter) {
		this.consumer = consumer;
		emitter.addListener(consumer::run);
	}

}
