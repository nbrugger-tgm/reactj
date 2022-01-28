package com.niton.reactj.api.diff;

import java.util.SortedSet;

/**
 * A tool to create a diff of something
 *
 * @param <T> the type to generate the diff for (String,List etc)
 * @param <D> the type of the atomic changes to be reported
 */
public interface DiffTool<T, D extends DiffEntry<T>> {
    /**
     * Calculates the difference between two objects and returns the changes
     * that have to be made to {@code origin} to get {@code modified}.
     *
     * @param origin   the object state before the changes
     * @param modified the object state after the changes
     *
     * @return the changes that have to be made to {@code origin} to get {@code modified}
     */
    SortedSet<D> diff(T origin, T modified);
}
