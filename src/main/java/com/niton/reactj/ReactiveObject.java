package com.niton.reactj;

import java.util.ArrayList;
import java.util.List;

public class ReactiveObject {
	protected final List<ReactiveController> listeners = new ArrayList<>();

	void bind(ReactiveController c){
		listeners.add(c);
	}
	void unbind(ReactiveController c){
		listeners.remove(c);
	}
	protected void react(){
		listeners.forEach(ReactiveController::modelChanged);
	}
}
