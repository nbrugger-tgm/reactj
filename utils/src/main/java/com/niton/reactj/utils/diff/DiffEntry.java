package com.niton.reactj.utils.diff;

public interface DiffEntry<D> {
	/**
	 * Applies the change to the given object
	 *
	 * @param subject the object to effect with the change
	 */
	void applyTo(D subject);
}
