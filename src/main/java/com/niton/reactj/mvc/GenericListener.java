package com.niton.reactj.mvc;

public interface GenericListener extends Listener<Object>{
	void onAction();

	@Override
	default void onAction(Object event){
		onAction();
	}
}
