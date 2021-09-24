package com.niton.reactj.utils.event;

import com.niton.reactj.api.event.Listener;

public interface GenericListener extends Listener<Object> {
	@Override
	default void onAction(Object event) {
		onAction();
	}

	void onAction();
}
