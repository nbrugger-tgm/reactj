package com.niton.reactj.lists.test;

import com.niton.reactj.lists.ListChange;

import java.util.List;

@FunctionalInterface
public interface DiffFunction<E> {
	List<ListChange<E>> diff(List<E> oldList, List<E> newList);
}
