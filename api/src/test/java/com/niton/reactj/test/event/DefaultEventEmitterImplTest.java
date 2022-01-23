package com.niton.reactj.test.event;

import com.niton.reactj.api.event.CustomEventEmitter;
import com.niton.reactj.api.event.EventEmitter;
import com.niton.reactj.api.event.Listener;
import com.niton.reactj.testing.event.EventEmitterImplTest;
import org.junit.jupiter.api.DisplayName;

@DisplayName("DefaultEventEmitter")
class DefaultEventEmitterImplTest extends EventEmitterImplTest<Object, Listener<Object>> {

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