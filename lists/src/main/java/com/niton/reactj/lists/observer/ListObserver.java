package com.niton.reactj.lists.observer;

import com.niton.reactj.api.observer.Observer;
import com.niton.reactj.lists.diff.ListChange;
import com.niton.reactj.lists.diff.ListDiff;
import com.niton.reactj.lists.diff.ListDiffTool;

import java.util.ArrayList;
import java.util.List;

public class ListObserver<T> extends Observer<ListChange<T>, List<T>> {
	private final List<T>         lastState = new ArrayList<>();
	private final ListDiffTool<T> git       = new ListDiffTool<>();

	@Override
	public void update() {
		ListDiff<T> diff = git.diff(lastState, getObserved());
		diff.applyChanges(lastState);
		diff.forEach(this::fireObservation);
	}

	@Override
	public void reset() {
		lastState.clear();
		if (isObservingRebind())
			update();
	}
}
