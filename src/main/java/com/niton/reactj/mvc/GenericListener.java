package com.niton.reactj.mvc;

public interface GenericListener extends Listener<Object> {
	@Override
	default void onAction(Object event) {
		onAction();
	}

	void onAction();
}
