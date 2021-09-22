package com.niton.reactj.api.exceptions;

public class ReactiveAccessException extends ReactiveException {
	public ReactiveAccessException(IllegalAccessException fail) {
		super("Proxy method couldn't be accessed (make sure you open your module ot reactj.core)", fail);
	}
}
