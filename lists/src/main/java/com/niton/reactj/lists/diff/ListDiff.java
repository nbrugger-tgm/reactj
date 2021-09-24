package com.niton.reactj.lists.diff;

import java.util.List;
import java.util.TreeSet;

/**
 * Changelog of two lists
 *
 * @param <T> the element type of the lists
 */
public class ListDiff<T> extends TreeSet<ListChange<T>> {
	/**
	 * Applies all changes to the given list
	 *
	 * @param list the list to modify
	 */
	public void applyChanges(List<T> list) {
		forEach(ch -> ch.applyTo(list));
	}
}
