package com.niton.reactj.event.test;

import com.niton.reactj.event.CustomEventManager;
import com.niton.reactj.event.EventManager;
import com.niton.reactj.event.Listener;

class DefaultEventManagerTest extends EventManagerTest<Object, Listener<Object>> {

    @Override
    protected CustomEventManager<Object, Listener<Object>> createManager() {
        return new EventManager<>();
    }

    @Override
    protected Listener<Object> createListener(Runnable testCallback) {
        return ev->testCallback.run();
    }

    @Override
    protected Object getDummyEvent() {
        return new Object();
    }
}