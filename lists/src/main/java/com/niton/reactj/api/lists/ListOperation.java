package com.niton.reactj.api.lists;

import com.niton.reactj.lists.diff.ListChange;

import java.util.List;

/**
 * Defines an operation/change that can be carried out an a list
 */
public enum ListOperation {
	ADD {
		@Override
		public <T> void apply(List<T> list, int index, T elem) {
			list.add(index, elem);
		}
	},
	REMOVE {
		@Override
		public <T> void apply(List<T> list, int index, T elem) {
			if (list.remove(index) != elem)
				throw new IllegalArgumentException("Remove operation element at index didn't equal element from change (list.remove(index) != expectedElement)");
		}
	};

	/**
	 * Execute the change onto the list (the {@link ListChange#getOperation()} is ignored
	 */
	public abstract <T> void apply(List<T> list, int index, T element);
}
