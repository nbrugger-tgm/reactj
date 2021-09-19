package com.niton.reactj.api.proxy;

public class ProxyException extends RuntimeException {
	public ProxyException(String message) {
		super(message);
	}

	public ProxyException(String message, Throwable cause) {
		super(message, cause);
	}
}
