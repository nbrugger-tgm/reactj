package com.niton.reactj.test.mvc;

import com.niton.reactj.api.event.EventEmitter;
import com.niton.reactj.api.mvc.ReactiveComponent;
import com.niton.reactj.api.observer.Observer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReactiveComponentTest {
    private static boolean updateCalled = false;
    private static String  bindingCalled;
    private static boolean onUiUpdate   = false;

    @BeforeEach
    void init() {
        updateCalled = false;
        bindingCalled = null;
        onUiUpdate = false;
    }

    @Test
    void constructing() {
        assertDoesNotThrow((MockComponent::new));
    }

    @Test
    void noViewNoBinding() {
        var component = new MockComponent();
        var observer  = (MockObserver) component.getObserver();
        component.onUiUpdate.listen(() -> onUiUpdate = true);
        observer.update();
        assertTrue(updateCalled);
        assertNotEquals(
                "test", bindingCalled,
                "The component is not initialized yet" +
                        " and thus the binding shut not be called"
        );
        assertFalse(
                onUiUpdate,
                "The component is not initialized yet, so the onUiUpdate should not be called"
        );
    }

    @Test
    void getView() {
        var component = new MockComponent();
        var observer  = (MockObserver) component.getObserver();
        component.onUiUpdate.listen(() -> onUiUpdate = true);
        assertEquals(12, component.getView(),
                     "getView() should return the value from the " +
                             "implementation of createView()"
        );
        observer.update();
        assertTrue(updateCalled);
        assertNotNull(
                bindingCalled,
                "When getView is called the bindings should be initialized" +
                        " and therefore the binding should be called on update()"
        );
        assertEquals(
                "test", bindingCalled,
                "The value received by the binding should be the value of the last update()"
        );
        assertTrue(onUiUpdate, "Every update() should trigger the onUiUpdate()");
    }

    @Test
    void modelStoring() {
        var component = new MockComponent();
        var observer  = (MockObserver) component.getObserver();
        assertEquals(
                observer.getObserved(), component.getModel(),
                "The model should be the same as the observed value"
        );
        assertThrows(IllegalArgumentException.class, () -> component.setModel(null));
        assertEquals(
                observer.getObserved(), component.getModel(),
                "The model should be the same as the observed value"
        );
        component.setModel("test");
        assertEquals(
                "test", observer.getObserved(),
                "The model should be the same as the observed value"
        );
        assertEquals(
                observer.getObserved(), component.getModel(),
                "The model should be the same as the observed value"
        );
    }

    private static class MockObserver extends Observer<String, String> {

        @Override
        protected void update() {
            updateCalled = true;
            fireObservation("test");
        }

        @Override
        public void reset() {
            updateCalled = false;
            if (isObservingRebind())
                update();
        }
    }

    private static class MockComponent extends ReactiveComponent<String, String, Integer> {

        protected MockComponent() {
            super(new MockObserver());
        }

        @Override
        protected Integer createView() {
            return 12;
        }

        @Override
        protected void registerBindings(EventEmitter<String> onObservation) {
            onObservation.listen(s -> bindingCalled = s);
        }

        public Observer<String, String> getObserver() {
            return observer;
        }
    }
}