package com.niton.reactj.api.exceptions;

import com.niton.reactj.api.react.Reactable;

public class SubjectCallException extends ReactiveException {
	public SubjectCallException() {
		super("ProxySubjects can't be called directly (without proxy)");
	}
}
