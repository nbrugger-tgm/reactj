package com.niton.reactj.lists;

import java.util.List;
import java.util.Objects;

import static com.niton.reactj.lists.Operation.ADD;
import static com.niton.reactj.lists.Operation.REMOVE;

public class ListChange<T> implements Comparable<ListChange<T>> {
	private final Operation operation;
	private final int       index;
	private final T         element;

	public ListChange(Operation operation, int index, T element) {
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

	public void apply(List<T> list) {
		operation.apply(list, this);
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

	public Operation getOperation() {
		return operation;
	}
}
