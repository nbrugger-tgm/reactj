package com.niton.reactj.lists.diff;

import com.niton.reactj.api.diff.DiffEntry;

import java.util.List;
import java.util.Objects;

import static com.niton.reactj.lists.diff.ListOperation.ADD;
import static com.niton.reactj.lists.diff.ListOperation.REMOVE;

/**
 * Describes a change to a list
 *
 * @param <T> the element type of the list
 */
public class ListChange<T> implements DiffEntry<List<T>>, Comparable<ListChange<T>> {
	private final ListOperation operation;
	private final int           index;
	private final T             element;

	/**
	 * @param operation what to do with the
	 * @param index     the index the change took place at
	 * @param element   the inserted/deleted element
	 */
	public ListChange(ListOperation operation, int index, T element) {
		this.operation = operation;
		this.index = index;
		this.element = element;
	}

	public T getElement() {
		return element;
	}

	@Override
	public int hashCode() {
		return Objects.hash(operation, index, element);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ListChange<?> that = (ListChange<?>) o;
		return index == that.index && operation == that.operation && Objects.equals(element, that.element);
	}

	@Override
	public String toString() {
		return (operation == ADD ? "add(" : "remove(") + element + ", " + index + ")";
	}

	/**
	 * Applies the change to the list. Therefore, the list will be directly modified
	 *
	 * @param list the list to modify
	 */
	@Override
	public void applyTo(List<T> list) {
		operation.apply(list, index, element);
	}

	@Override
	public int compareTo(ListChange<T> o) {
		int indexComp = getIndex() - o.getIndex();
		if (indexComp != 0)
			return indexComp;
		if (getOperation() == REMOVE)
			return -1;
		else
			return 1;
	}

	public int getIndex() {
		return index;
	}

	public ListOperation getOperation() {
		return operation;
	}

}
