package com.niton.reactj.objects.dsl.impl;

import com.niton.reactj.api.event.EventEmitter;
import com.niton.reactj.objects.dsl.ObjectConsumerDsl;
import com.niton.reactj.objects.dsl.ObjectDsl;
import com.niton.reactj.objects.dsl.ObjectRunnableDsl;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ObjectDslImpl<T> implements ObjectDsl<T> {
	private final T               model;
	private final EventEmitter<T> objectChangeEvent;

	public ObjectDslImpl(T model, EventEmitter<T> objectChangeEvent) {
		this.model             = model;
		this.objectChangeEvent = objectChangeEvent;
	}

	@Override
	public ObjectConsumerDsl call(BiConsumer setter) {
		return null;
	}

	@Override
	public ObjectRunnableDsl call(Runnable runnable) {
		return null;
	}

	@Override
	public ObjectConsumerDsl call(Consumer runnable) {
		return null;
	}
}
