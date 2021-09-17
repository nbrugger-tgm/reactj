package com.niton.reactj.api.proxy;


import com.niton.reactj.api.react.Reactable;
import com.niton.reactj.api.util.ReactiveReflectorUtil;
import com.niton.reactj.event.GenericEventManager;

import java.util.Map;

/**
 * <b>This interface does not adds any functionality!</b><br><br>
 * <p>
 * This interface should <b>only</b> be used when the class is constructed as proxy {@link
 * ProxyCreator#create(Object)}<br><br>
 * <p>
 * "implementing" this interface removes the need to use {@code ReactiveProxy<MyDataType>} as type definition
 */
public interface ProxySubject extends Reactable {
	
	@Override
	default Map<String, Object> getState() {
		return ReactiveReflectorUtil.getState(this);
	}

	@Override
	default void set(String property, Object value) {
		ReactiveReflectorUtil.updateField(this,property,value);
	}

	@Override
	default GenericEventManager reactEvent(){return null;}
}
