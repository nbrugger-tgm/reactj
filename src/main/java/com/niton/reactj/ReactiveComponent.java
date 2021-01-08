package com.niton.reactj;

import com.niton.reactj.annotation.ReactivResolution;
import com.niton.reactj.annotation.Reactive;
import com.niton.reactj.exceptions.ReactiveException;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

import static com.niton.reactj.annotation.ReactivResolution.ReactiveResolutions.DEEP;

/**
 * Used to create a component that reacts to a Reactable Model. Most likely used for Views and similar stuff.<br>
 *     Annotate methods with @Reactive for automatic bindings
 */
public interface ReactiveComponent {
	/**
	 * Add bindings that define how to react to changes in the model.<br>
     * Use {@code binder.<bindingMethod>} to bind a method to a chang<br><br>
	 * <b>Example:</b><br>
	 * {@code
	 *   binder.bind("name",nameLabel::setText);
	 * }<br>
	 * This would bind the name property to the name label -> name label always displays the name
	 * @param binder the binder to bind to (used to create the bindings)
	 */
	void createBindings(ReactiveBinder binder);
}
