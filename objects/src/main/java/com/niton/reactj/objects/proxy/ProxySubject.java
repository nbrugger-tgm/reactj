package com.niton.reactj.objects.proxy;


import com.niton.reactj.api.event.GenericEventEmitter;
import com.niton.reactj.api.react.Reactable;
import com.niton.reactj.objects.exceptions.SubjectCallException;
import com.niton.reactj.objects.reflect.Reflective;

import java.util.Map;

/**
 * <b>This interface does not adds any functionality!</b><br><br>
 * <p>
 * This interface should <b>only</b> be used when the class is constructed as proxy {@link
 * ProxyCreator#create(Object)}<br><br>
 * <p>
 * "implementing" this interface removes the need to use {@code ReactiveProxy<MyDataType>} as type
 * definition
 */
public interface ProxySubject extends Reactable, Reflective {
    @Override
    default GenericEventEmitter reactEvent() {
        throw new SubjectCallException();
    }

    @Override
    default void set(String property, Object value) {
        throw new SubjectCallException();
    }

    @Override
    default Map<String, Object> getState() {
        throw new SubjectCallException();
    }
}
