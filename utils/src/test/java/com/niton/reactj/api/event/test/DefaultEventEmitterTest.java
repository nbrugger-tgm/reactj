package com.niton.reactj.api.event.test;

import com.niton.reactj.api.event.CustomEventEmitter;
import com.niton.reactj.api.event.EventEmitter;
import com.niton.reactj.api.event.Listener;
import com.niton.reactj.testing.event.EventEmitterTest;
import org.junit.jupiter.api.DisplayName;

@DisplayName("DefaultEventEmitter")
class DefaultEventEmitterTest extends EventEmitterTest<Object, Listener<Object>> {

	@Override
	protected CustomEventEmitter<Object, Listener<Object>> createManager() {
		return new EventEmitter<>();
	}

	@Override
	protected Listener<Object> createListener(Runnable testCallback) {
		return ev -> testCallback.run();
	}

	@Override
	protected Object getDummyEvent() {
		return new Object();
	}
}