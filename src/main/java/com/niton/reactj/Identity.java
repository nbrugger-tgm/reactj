package com.niton.reactj;

/**
 * With this interface present the objects can be called appart by a single method ("like a primary key")
 * @param <T> type of the Unique key
 */
public interface Identity<T> {
	/**
	 *
	 * @return The unique ID that sets this object appart from every else
	 */
	public T getID();
}
