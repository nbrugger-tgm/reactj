package com.niton.reactj.test.dsl;

import com.niton.reactj.api.binding.dsl.BinderDsl;
import com.niton.reactj.api.binding.predicates.Condition;
import com.niton.reactj.api.event.EventEmitter;
import com.niton.reactj.api.event.GenericEventEmitter;
import com.niton.reactj.objects.dsl.ObjectDsl;
import com.niton.reactj.test.models.Base;
import com.niton.reactj.testing.binding.DslTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class ObjectDslTest extends DslTest {
    Base               base;
    EventEmitter<Base> emitter = new EventEmitter<>();
    ObjectDsl<Base>    dsl     = ObjectDsl.create(() -> base, emitter);

    @BeforeEach
    void setUp() {
        base = new Base();
    }

    @Test
    void bindNonModel() {
        AtomicInteger aCaptor   = new AtomicInteger();
        AtomicInteger bCaptor   = new AtomicInteger();
        var           localBase = new Base();
        localBase.setA(5);
        localBase.setB(10);
        var trigger = new GenericEventEmitter();
        dsl.bind(aCaptor::set, localBase::getA)
           .on(trigger);
        dsl.bind(bCaptor::set, localBase::getB)
           .on(trigger);
        assertEquals(0, aCaptor.get(), "The binding itself should not have changed the value");
        assertEquals(0, bCaptor.get(), "The binding itself should not have changed the value");

        trigger.fire();

        assertEquals(
                5, aCaptor.get(),
                "The binding should have changed the value, after the event was fired"
        );
        assertEquals(
                10, bCaptor.get(),
                "The binding should have changed the value, after the event was fired"
        );
    }

    @Test
    void bindNonModelWithConverter() {
        AtomicReference<String> string    = new AtomicReference<>();
        var                     localBase = new Base();
        var                     trigger   = new GenericEventEmitter();

        localBase.setA(123);

        dsl.bind(string::set, localBase::getA, Object::toString)
           .on(trigger);

        assertNull(string.get(), "The binding itself should not have changed the value");

        trigger.fire();

        assertEquals(
                "123", string.get(),
                "The binding should have changed the value, after the event was fired"
        );

    }

    @Test
    void bindWithModelGetter() {
        AtomicInteger string  = new AtomicInteger();
        var           trigger = new GenericEventEmitter();
        dsl.bind(string::set, Base::getA)
           .on(trigger);
        base.setA(321);
        trigger.fire();
        assertEquals(
                321,
                string.get(),
                "The binding should have called the setter of the bound model," +
                        " after the event was fired"
        );
    }

    @Test
    void bindWithModelGetterAndConverter() {
        AtomicReference<String> string  = new AtomicReference<>();
        var                     trigger = new GenericEventEmitter();

        base.setA(123);

        dsl.bind(string::set, Base::getA, Object::toString)
           .on(trigger);

        assertNull(string.get(), "The binding itself should not have changed the value");

        trigger.fire();

        assertEquals(
                "123", string.get(),
                "The binding should have changed the value, after the event was fired"
        );

    }

    @Test
    void bindWithModelSetter() {
        AtomicInteger getter  = new AtomicInteger();
        var           trigger = new GenericEventEmitter();

        getter.set(654);

        dsl.bind(Base::setA, getter::get)
           .on(trigger);

        assertEquals(0, base.getA(), "The binding itself should not have changed the value");

        trigger.fire();

        assertEquals(
                654, base.getA(),
                "The binding should have changed the value, after the event was fired"
        );
    }


    @Test
    void callMethodSetter() {
        AtomicInteger getter  = new AtomicInteger();
        var           trigger = new GenericEventEmitter();
        dsl.call(Base::setA)
           .with(getter::get)
           .on(trigger);
        getter.set(321);
        trigger.fire();
        assertEquals(
                321,
                base.getA(),
                "The binding should have called the setter of the bound model," +
                        " after the event was fired"
        );
    }

    @Test
    void onModelEvent() {
        AtomicInteger store = new AtomicInteger(999);
        dsl.call(store::set)
           .withValue(111)
           .onModelChange();
        assertEquals(999, store.get(), "The binding itself should not have changed the value");
        emitter.fire(base);
        assertEquals(111, store.get(), "When the event given to the factory method 'create' is " +
                "fired, the binding should be executed");
    }

    @Test
    void callWithModel() {
        AtomicReference<Base> baseStore = new AtomicReference<>();
        var runnable = dsl.call(baseStore::set)
                          .withModel(Function.identity())
                          .build();
        runnable.run();
        assertSame(
                base,
                baseStore.get(),
                "withModel should use the model given to the factory method"
        );
    }

    @Test
    void onModelChange() {
        AtomicInteger store = new AtomicInteger(999);
        dsl.call(store::set)
           .onModelChange(Base::getA);
        assertEquals(999, store.get(), "The binding itself should not have changed the value");
        base.setA(1234);
        emitter.fire(base);
        assertEquals(
                base.getA(),
                store.get(),
                "The binding should be executed with the model given to the factory method. when the event givent to the factory method 'create' is fired"
        );
    }

    @Test
    void conditionalRunnableOnModelChange() {
        AtomicInteger store = new AtomicInteger(999);
        dsl.call(() -> store.set(1234))
           .when(Condition.YES)
           .onModelChange();
        assertEquals(999, store.get(), "The binding itself should not have changed the value");
        emitter.fire(base);
        assertEquals(
                1234, store.get(),
                "The binding should be executed with the model given to the factory method." +
                        "When the event given to the factory method 'create' is fired"
        );
    }

    @Test
    void conditionalBindingOnModelChange() {
        AtomicInteger store = new AtomicInteger(999);
        dsl.call(store::set)
           .withValue(123)
           .when(Condition.YES)
           .onModelChange();
        assertEquals(999, store.get(), "The binding itself should not have changed the value");
        emitter.fire(base);
        assertEquals(
                123, store.get(),
                "The binding should be executed with the model given to the factory method." +
                        "When the event given to the factory method 'create' is fired"
        );
    }

    @Test
    void runnableOnModelChange() {
        AtomicInteger store = new AtomicInteger(999);
        dsl.call(() -> store.set(1234))
           .onModelChange();
        assertEquals(999, store.get(), "The binding itself should not have changed the value");
        emitter.fire(base);
        assertEquals(
                1234, store.get(),
                "The binding should be executed with the model given to the factory method." +
                        "When the event given to the factory method 'create' is fired"
        );
    }

    @Override
    protected BinderDsl createBinder() {
        return ObjectDsl.create(() -> base, emitter);
    }
}