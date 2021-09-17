package com.niton.reactj.api.test;

import com.niton.reactj.api.react.ReactiveObject;
import com.niton.reactj.api.observer.ObjectObserver;
import com.niton.reactj.api.observer.ObjectObserver.PropertyObservation;
import com.niton.reactj.api.test.ObjectObserverTest.Subject;
import com.niton.reactj.observer.testing.ObserverImplTest;

class ObjectObserverTest extends ObserverImplTest<ObjectObserver<Subject>, PropertyObservation,Subject> {

	@Override
	protected ObjectObserver<Subject> createObserverInstance() {
		return new ObjectObserver<>();
	}

	@Override
	protected Subject createObservableInstance() {
		return new Subject();
	}

	@Override
	protected PropertyObservation modify(Subject observable){
		observable.setVariable(observable.getVariable()+1);
		return new PropertyObservation("variable",observable.getVariable());
	}

	static class Subject extends ReactiveObject {
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