package com.niton.reactj.api.exceptions;

public class SubjectCallException extends ReactiveException {
	public SubjectCallException() {
		super("ProxySubjects can't be called directly (without proxy)");
	}
}
