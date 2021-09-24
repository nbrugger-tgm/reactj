package com.niton.reactj.utils.exceptions;

public class ReflectiveCallException extends RuntimeException {
	public ReflectiveCallException(String message) {
		super(message);
	}

	public ReflectiveCallException(String message, Throwable cause) {
		super(message, cause);
	}
}
