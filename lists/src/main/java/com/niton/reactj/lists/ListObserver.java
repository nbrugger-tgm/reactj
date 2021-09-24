package com.niton.reactj.lists;

import com.niton.reactj.observer.AbstractObserver;

import java.util.ArrayList;
import java.util.List;

import static com.niton.reactj.lists.Operation.ADD;
import static com.niton.reactj.lists.Operation.REMOVE;

public class ListObserver<T> extends AbstractObserver<ListChange<T>, List<T>> {
	private final List<T> lastState = new ArrayList<>();

	private ListObserver() {
		addListener(e -> {
			if (e.getOperation() == ADD)
				lastState.add(e.getIndex(), e.getElement());
			if (e.getOperation() == REMOVE)
				lastState.remove(e.getIndex());
		});
	}

	@Override
	public void stopObservation() {

	}

	@Override
	protected void update() {
		//List<ListChange> added = ListDiffUtil.addedDiff(lastState, getObserved());

	}


	@Override
	public void reset() {

	}
}
