package com.niton.reactj.test.lists;

import com.niton.reactj.core.observer.ObserverImplTest;
import com.niton.reactj.lists.diff.ListChange;
import com.niton.reactj.lists.observer.ListObserver;
import com.niton.reactj.lists.proxy.ListProxyCreator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.niton.reactj.api.lists.ListOperation.ADD;

@DisplayName("ListObserver")
class ListObserverTest extends ObserverImplTest<ListObserver<Integer>, ListChange<Integer>, List<Integer>> {
	private final ListProxyCreator creator = new ListProxyCreator();

	@Override
	protected ListObserver<Integer> createObserverInstance() {
		return new ListObserver<>();
	}

	@Test
	void diffTest() {
		getObserver().observe(createObservableInstance());
		ListChange<Integer> ch = modify(getObserver().getObserved());
		getObserver().update();
		assertEquals(ch, getFired());
	}

	@Override
	protected List<Integer> createObservableInstance() {
		return creator.create(new ArrayList<>());
	}

	@Override
	protected ListChange<Integer> modify(List<Integer> observable) {
		observable.add(105);
		return new ListChange<>(ADD, observable.size() - 1, 105);
	}

}
