package com.niton.reactj.test.lists;


import com.niton.reactj.api.binding.dsl.BinderDsl;
import com.niton.reactj.api.event.EventEmitter;
import com.niton.reactj.lists.diff.ListChange;
import com.niton.reactj.lists.mvc.ReactiveListComponent;
import com.niton.reactj.lists.proxy.ListProxyCreator;
import com.niton.reactj.testing.mvc.ReactiveComponentImplTest;

import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;

public class ReactiveListComponentTest
		extends
		ReactiveComponentImplTest<ReactiveListComponent<String, Object>, List<String>, ListChange<String>> {
	@Override
	protected ReactiveListComponent<String, Object> getComponent() {
		return new ReactiveListComponent<>() {
			@Override
			protected void createBindings(
					BinderDsl builder,
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
		var          creator = new ListProxyCreator(MethodHandles.lookup());
		List<String> strings = creator.create(new LinkedList<>());
		strings.add("1");
		strings.add("2");
		strings.add("3");
		return strings;
	}

	@Override
	protected void modify(List<String> model) {
		model.add("4");
	}
}
