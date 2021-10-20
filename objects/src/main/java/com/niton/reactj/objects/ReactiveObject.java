package com.niton.reactj.objects;


import com.niton.reactj.api.react.Reactable;
import com.niton.reactj.api.react.ReactiveForwarder;
import com.niton.reactj.api.react.ReactiveWrapper;

/**
 * The base class to make an Object reactive (usable in ReactiveComponents).
 * <p>
 * The most common way to use this component is by extending it and call {@link ReactiveObject#react()} whenever needed
 */
public class ReactiveObject implements ReactiveForwarder {
	private final ReactiveWrapper<ReactiveObject> thisWrapper = new ReactiveWrapper<>(this);

	@Override
	public Reactable getReactableTarget() {
		return thisWrapper;
	}

}
