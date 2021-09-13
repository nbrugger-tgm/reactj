package com.niton.reactj;

import com.niton.reactj.mvc.GenericEventManager;

import java.util.Map;

/**
 * The base class to make an Object reactive (usable in ReactiveComponents).
 * <p>
 * The most common way to use this component is by extending it and call {@link ReactiveObject#react()} whenever needed
 */
public class ReactiveObject implements Reactable {
	private final ReactiveWrapper<ReactiveObject> thisWrapper = new ReactiveWrapper<>(this);

	/**
	 * Only use this constructor when extending from this class
	 */
	protected ReactiveObject() {}

	@Override
	public Map<String, Object> getState() {
		return thisWrapper.getState();
	}

	@Override
	public GenericEventManager reactEvent() {
		return thisWrapper.reactEvent();
	}

	@Override
	public void set(Map<String, Object> changed) {
		thisWrapper.set(changed);
	}

	@Override
	public void set(String property, Object value) throws Exception {
		thisWrapper.set(property, value);
	}
}
