package com.niton.reactj.api.event;

public interface GenericListener extends Listener<Object> {
	@Override
	default void onAction(Object event) {
		onAction();
	}

	void onAction();
}
