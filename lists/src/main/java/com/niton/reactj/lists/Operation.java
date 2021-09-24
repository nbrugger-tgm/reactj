package com.niton.reactj.lists;

import java.util.List;

public enum Operation {
	ADD {
		@Override
		public <T> void apply(List<T> list, ListChange<T> change) {
			list.add(change.getIndex(), change.getElement());
		}
	},
	REMOVE {
		@Override
		public <T> void apply(List<T> list, ListChange<T> change) {
			if (list.remove(change.getIndex()) != change.getElement()) ;
			//throw new IllegalArgumentException("Remove operation element at index didn't equal element from change");
		}
	};

	public abstract <T> void apply(List<T> list, ListChange<T> change);
}
