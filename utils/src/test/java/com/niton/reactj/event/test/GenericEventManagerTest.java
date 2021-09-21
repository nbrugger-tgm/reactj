package com.niton.reactj.event.test;

import com.niton.reactj.event.CustomEventManager;
import com.niton.reactj.event.GenericEventManager;
import com.niton.reactj.event.GenericListener;

class GenericEventManagerTest extends EventManagerTest<Void, GenericListener> {

    @Override
    protected CustomEventManager<Void, GenericListener> createManager() {
        return new GenericEventManager();
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