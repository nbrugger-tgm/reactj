package com.niton.reactj;

import com.niton.reactj.annotation.Unreactive;

import java.util.ArrayList;
import java.util.List;

public class ReactiveObject {
	@Unreactive
	protected final List<ReactiveController<?>> listeners = new ArrayList<>();

	void bind(ReactiveController<?> c){
		listeners.add(c);
	}
	void unbind(ReactiveController<?> c){
		listeners.remove(c);
	}
	protected void react(){
		listeners.forEach(ReactiveController::modelChanged);
	}
}
