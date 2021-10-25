package com.niton.reactj.test.lists;


import com.niton.reactj.api.event.EventEmitter;
import com.niton.reactj.implementation.binding.ModelCallBuilder;
import com.niton.reactj.implementation.binding.ReactiveBinder;
import com.niton.reactj.lists.diff.ListChange;
import com.niton.reactj.lists.mvc.ReactiveListComponent;
import com.niton.reactj.testing.mvc.ReactiveComponentImplTest;

import java.util.List;

public class ReactiveListComponentTest
		extends ReactiveComponentImplTest<ReactiveListComponent<String, Object>, List<String>, ListChange<String>> {
	@Override
	protected ReactiveListComponent<String, Object> getComponent() {
		return new ReactiveListComponent<>() {
			@Override
			protected void createBindings(
					ReactiveBinder<ModelCallBuilder<List<String>>> builder,
					EventEmitter<Integer> onRemove,
					EventEmitter<Integer> onAdd,
					EventEmitter<ListChange<String>> anyListChange
			) {
				//not needed for tests
			}

			@Override
			protected Object createView() {
				return new Object();
			}
		};
	}

	@Override
	protected List<String> getExpectedInitialModel() {
		return null;
	}

	@Override
	protected List<String> generateObservable() {
		return null;
	}

	@Override
	protected void modify(List<String> strings) {

	}
}
