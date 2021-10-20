package com.niton.reactj.test.api.event;

import com.niton.reactj.api.event.CustomEventEmitter;
import com.niton.reactj.testing.event.EventEmitterImplTest;
import com.niton.reactj.utils.event.GenericEventEmitter;
import com.niton.reactj.utils.event.GenericListener;
import org.junit.jupiter.api.DisplayName;

@DisplayName("GenericEventEmitter")
class GenericEventEmitterImplTest extends EventEmitterImplTest<Object, GenericListener> {

	@Override
	protected CustomEventEmitter<Object, GenericListener> createManager() {
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