package com.niton.reactj.api.binding;

@FunctionalInterface
public interface Listenable {
	void listen(Runnable listener);
}
