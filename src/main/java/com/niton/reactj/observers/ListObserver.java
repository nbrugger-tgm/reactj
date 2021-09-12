package com.niton.reactj.observers;

import com.niton.reactj.special.ListAction;

import java.util.ArrayList;
import java.util.List;

public class ListObserver<E> extends AbstractObserver<ListObserver.ListObservation, List<E>> {
	private final List<E> valueCache = new ArrayList<>();

	public static class ListObservation {
		public final ListAction action;
		public final Object     content;
		public final int        index;

		protected ListObservation(ListAction action, Object content, int index) {
			this.action = action;
			this.content = content;
			this.index = index;
		}
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
