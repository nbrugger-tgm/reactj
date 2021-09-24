package com.niton.reactj.lists.observer;

import com.niton.reactj.api.observer.AbstractObserver;
import com.niton.reactj.lists.diff.ListChange;
import com.niton.reactj.lists.diff.ListDiffTool;

import java.util.ArrayList;
import java.util.List;

import static com.niton.reactj.api.lists.ListOperation.ADD;
import static com.niton.reactj.api.lists.ListOperation.REMOVE;

public class ListObserver<T> extends AbstractObserver<ListChange<T>, List<T>> {
	private final List<T>      lastState = new ArrayList<>();
	private final ListDiffTool diffTool  = new ListDiffTool();

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
	}


	@Override
	public void reset() {

	}
}
