package com.niton.reactj.test;

import com.niton.reactj.api.event.EventEmitter;
import com.niton.reactj.api.exceptions.ReactiveException;
import com.niton.reactj.objects.ReactiveObjectComponent;
import com.niton.reactj.objects.annotations.ReactiveListener;
import com.niton.reactj.objects.dsl.ObjectDsl;
import com.niton.reactj.objects.observer.PropertyObservation;
import com.niton.reactj.objects.proxy.ProxyCreator;
import com.niton.reactj.objects.proxy.ProxySubject;
import com.niton.reactj.test.ReactiveObjectComponentTest.Person;
import com.niton.reactj.testing.mvc.ReactiveComponentImplTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ReactiveObjectComponent")
class ReactiveObjectComponentTest
        extends ReactiveComponentImplTest<
        ReactiveObjectComponent<Person, Object>,
        ReactiveObjectComponentTest.Person,
        PropertyObservation<Person>
        > {
    private static ProxyCreator creator;
    private        int          received;
    private        boolean      called = false;

    @BeforeAll
    static void createCreator() {
        creator = ProxyCreator.besideOrigin();
        //only for testing purposes- plz don't do this <3
        creator.setAllowUnsafeProxies(true);
    }

    @Override
    protected ReactiveObjectComponent<Person, Object> getComponent() {
        return new ReactiveObjectComponent<>() {
            @Override
            protected void createBindings(
                    ObjectDsl<Person> builder,
                    EventEmitter<Person> onModelChange,
                    EventEmitter<PropertyObservation<Person>> onPropertyChange
            ) {
                //todo: yikes
            }

            @Override
            protected Object createView() {
                return new Object();
            }
        };
    }

    @Override
    protected Person getExpectedInitialModel() {
        return null;
    }

    @Override
    protected Person generateObservable() {
        return creator.create(new Person());
    }

    @Override
    protected void modify(Person model) {
        model.celebrateBirthday();
    }

    @Test
    @DisplayName("@ReactiveListener too many arguments")
    void tooManyReactiveListenerArgs() {
        var wrongImplementation = new ReactiveObjectComponent<Person, Object>() {

            @Override
            protected void createBindings(
                    ObjectDsl<Person> builder,
                    EventEmitter<Person> onModelChange,
                    EventEmitter<PropertyObservation<Person>> onPropertyChange
            ) {
                //ignore
            }

            @Override
            protected Object createView() {
                return new Object();
            }

            @ReactiveListener("age")
            private void badListener(int a, int b) {
                //ignore execution
            }
        };
        assertThrows(
                ReactiveException.class,
                wrongImplementation::getView,
                "@ReactiveListeners are not allowed to have more than one parameter," +
                        " and if they do, an exception should be thrown"
        );
    }

    @Test
    @DisplayName("@ReactiveListener bad argument")
    void reactiveListenerBadArgument() {
        var wrongImplementation = new ReactiveObjectComponent<Person, Object>() {

            @Override
            protected void createBindings(
                    ObjectDsl<Person> builder,
                    EventEmitter<Person> onModelChange,
                    EventEmitter<PropertyObservation<Person>> onPropertyChange
            ) {
                //ignore
            }

            @Override
            protected Object createView() {
                return new Object();
            }

            @ReactiveListener("age")
            private void badListener(String a) {
                //ignore execution
            }
        };
        var proxy = creator.create(new Person());
        wrongImplementation.getView();//creates bindings

        assertThrows(
                ClassCastException.class, () -> wrongImplementation.setModel(proxy),
                "When a @ReactiveListener has a type that doesn't matches the property" +
                        " it should fail to set such a model"
        );
    }

    @BeforeEach
    void reset() {
        received = -1;
        called   = false;
    }

    @Test
    @DisplayName("@ReactiveListener internal error")
    void reactiveListenerInternalError() {
        var wrongImplementation = new ReactiveObjectComponent<Person, Object>() {

            @Override
            protected void createBindings(
                    ObjectDsl<Person> builder,
                    EventEmitter<Person> onModelChange,
                    EventEmitter<PropertyObservation<Person>> onPropertyChange
            ) {
                //ignore
            }

            @Override
            protected Object createView() {
                return new Object();
            }

            @ReactiveListener("age")
            private void errorProne() {
                throw new IndexOutOfBoundsException("bad impl.");
            }
        };
        var proxy = creator.create(new Person());
        wrongImplementation.getView();//creates bindings

        assertThrows(
                ReactiveException.class, () -> wrongImplementation.setModel(proxy),
                "When a @ReactiveListener throws an exception," +
                        "the call that triggered the exception should throw an" +
                        "ReactiveException with the regarding cause"
        );
    }

    @Test
    @DisplayName("@ReactiveListener with argument")
    void reactiveListenerWithArgument() {
        var listeningComponent = new ReactiveObjectComponent<Person, Object>() {

            @Override
            protected void createBindings(
                    ObjectDsl<Person> builder,
                    EventEmitter<Person> onModelChange,
                    EventEmitter<PropertyObservation<Person>> onPropertyChange
            ) {
                //ignored
            }

            @Override
            protected Object createView() {
                return new Object();
            }

            @ReactiveListener("age")
            private void setReceived(int age) {
                received = age;
            }
        };
        var proxy = creator.create(new Person());
        listeningComponent.getView();//creates bindings
        listeningComponent.setModel(proxy);
        proxy.celebrateBirthday();

        assertEquals(proxy.getAge(), received, "@ReactiveListener was not properly called");
    }

    @Test
    @DisplayName("@ReactiveListener without argument")
    void reactiveListenerWithoutArgument() {
        var listeningComponent = new ReactiveObjectComponent<Person, Object>() {

            @Override
            protected void createBindings(
                    ObjectDsl<Person> builder,
                    EventEmitter<Person> onModelChange,
                    EventEmitter<PropertyObservation<Person>> onPropertyChange
            ) {
                //ignored
            }

            @Override
            protected Object createView() {
                return new Object();
            }

            @ReactiveListener("age")
            private void intercept() {
                called = true;
            }
        };
        var proxy = creator.create(new Person());
        listeningComponent.getView();//creates bindings
        listeningComponent.setModel(proxy);
        proxy.celebrateBirthday();

        assertTrue(called, "@ReactiveListener was not properly called");
    }

    @Test
    void bindings() {
        var listeningComponent = new ReactiveObjectComponent<Person, Object>() {

            @Override
            protected void createBindings(
                    ObjectDsl<Person> builder,
                    EventEmitter<Person> onModelChange,
                    EventEmitter<PropertyObservation<Person>> onPropertyChange
            ) {
                if (builder == null)
                    fail("builder in createBinding is not allowed to be null");
                onModelChange.listen(p -> called = true);
                onModelChange.listen(p -> received = p.getAge());
            }

            @Override
            protected Object createView() {
                return new Object();
            }
        };
        var proxy = creator.create(new Person());
        listeningComponent.getView();//creates bindings
        listeningComponent.setModel(proxy);
        proxy.celebrateBirthday();
        assertTrue(called);
        assertEquals(proxy.getAge(), received, "createBindings() was not properly called");
    }

    protected static class Person implements ProxySubject {
        private int age = 18;

        public void celebrateBirthday() {
            age++;
        }

        public int getAge() {
            return age;
        }
    }
}
