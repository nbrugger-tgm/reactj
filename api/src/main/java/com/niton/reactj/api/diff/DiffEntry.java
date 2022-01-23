package com.niton.reactj.api.diff;

/**
 * Describes a change to an object that was or will be made to an object
 *
 * @param <D> the object to modify
 */
public interface DiffEntry<D> {
	/**
	 * Applies the change to the given object
	 *
	 * @param subject the object to effect with the change
	 */
	void applyTo(D subject);
}
