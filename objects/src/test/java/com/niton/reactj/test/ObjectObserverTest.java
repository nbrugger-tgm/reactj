package com.niton.reactj.test;

import com.niton.reactj.api.react.ReactiveObject;
import com.niton.reactj.core.observer.ObjectObserver;
import com.niton.reactj.core.observer.ObserverImplTest;
import com.niton.reactj.core.observer.PropertyObservation;
import com.niton.reactj.core.observer.SelfReflective;
import com.niton.reactj.test.ObjectObserverTest.Subject;
import org.junit.jupiter.api.DisplayName;

@DisplayName("ObjectObserver")
class ObjectObserverTest extends ObserverImplTest<ObjectObserver<Subject>, PropertyObservation<Subject>, Subject> {

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
}