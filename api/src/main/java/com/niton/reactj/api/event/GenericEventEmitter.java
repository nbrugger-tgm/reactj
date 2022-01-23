package com.niton.reactj.api.event;


import com.niton.reactj.api.binding.Listenable;

/**
 * An event emitter that emits events without data
 */
public class GenericEventEmitter
		extends CustomEventEmitter<Object, Runnable>
		implements Listenable {
	@Override
	protected void call(Runnable listener, Object event) {
		listener.run();
	}

	public void fire() {
		super.fire(null);
	}
}
