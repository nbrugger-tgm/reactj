package com.niton.reactj.test;

import com.niton.reactj.api.react.ReactiveObject;
import com.niton.reactj.objects.observer.ObjectObserver;
import com.niton.reactj.objects.observer.PropertyObservation;
import com.niton.reactj.objects.reflect.SelfReflective;
import com.niton.reactj.test.ObjectObserverTest.Subject;
import com.niton.reactj.testing.observer.ObserverImplTest;
import org.junit.jupiter.api.DisplayName;

@DisplayName("ObjectObserver")
class ObjectObserverTest
        extends ObserverImplTest<ObjectObserver<Subject>, PropertyObservation<Subject>, Subject> {

    @Override
    protected ObjectObserver<Subject> createObserverInstance() {
        return new ObjectObserver<>();
    }

    @Override
    protected Subject createObservableInstance() {
        return new Subject();
    }

    @Override
    protected PropertyObservation<Subject> modify(Subject observable) {
        observable.setVariable(observable.getVariable() + 1);
        return new PropertyObservation<>("variable", observable.getVariable(), observable);
    }

    static class Subject extends ReactiveObject implements SelfReflective {
        private int variable;

        public int getVariable() {
            return variable;
        }

        public void setVariable(int variable) {
            this.variable = variable;
            react();
        }
    }
}