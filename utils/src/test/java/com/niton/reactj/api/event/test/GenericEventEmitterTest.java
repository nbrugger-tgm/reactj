package com.niton.reactj.api.event.test;

import com.niton.reactj.api.event.CustomEventEmitter;
import com.niton.reactj.testing.event.EventEmitterTest;
import com.niton.reactj.utils.event.GenericEventEmitter;
import com.niton.reactj.utils.event.GenericListener;
import org.junit.jupiter.api.DisplayName;

@DisplayName("GenericEventEmitter")
class GenericEventEmitterTest extends EventEmitterTest<Void, GenericListener> {

	@Override
	protected CustomEventEmitter<Void, GenericListener> createManager() {
		return new GenericEventEmitter();
	}

	@Override
	protected GenericListener createListener(Runnable testCallback) {
		return testCallback::run;
	}

	@Override
	protected Void getDummyEvent() {
		return null;
	}
}