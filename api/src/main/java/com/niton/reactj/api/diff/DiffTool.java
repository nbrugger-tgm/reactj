package com.niton.reactj.api.diff;

import java.util.SortedSet;

/**
 * A tool to create a diff of something
 *
 * @param <T> the type to generate the diff for (String,List etc)
 * @param <D> the type of the atomic changes to be reported
 */
public interface DiffTool<T, D extends DiffEntry<T>> {
	SortedSet<D> diff(T origin, T modified);
}
