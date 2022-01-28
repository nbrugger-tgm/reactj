package com.niton.reactj.test.event;

import com.niton.reactj.api.event.CustomEventEmitter;
import com.niton.reactj.api.event.GenericEventEmitter;
import com.niton.reactj.testing.event.EventEmitterImplTest;
import org.junit.jupiter.api.DisplayName;

@DisplayName("GenericEventEmitter")
class GenericEventEmitterImplTest extends EventEmitterImplTest<Object, Runnable> {

    @Override
    protected CustomEventEmitter<Object, Runnable> createManager() {
        return new GenericEventEmitter();
    }

    @Override
    protected Runnable createListener(Runnable testCallback) {
        return testCallback;
    }

    @Override
    protected Void getDummyEvent() {
        return null;
    }
}