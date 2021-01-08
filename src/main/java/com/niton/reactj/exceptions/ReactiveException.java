package com.niton.reactj.exceptions;

/**
 * The general exception that is thrown when a known error occurs
 */
public class ReactiveException extends RuntimeException {

	public ReactiveException(String message) {
		super(message);
	}
}
