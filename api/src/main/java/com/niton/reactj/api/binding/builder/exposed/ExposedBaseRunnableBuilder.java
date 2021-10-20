package com.niton.reactj.api.binding.builder.exposed;

import com.niton.reactj.api.binding.builder.BaseRunnableBuilder;
import com.niton.reactj.api.event.EventEmitter;
import com.niton.reactj.api.event.GenericEventEmitter;

import java.util.function.Consumer;

public interface ExposedBaseRunnableBuilder<T extends Runnable> {
	/**
	 * Append a new execution statement to this binding
	 */
	ExposedReactiveBinder andAlso();

	/**
	 * Same as {@link #on(GenericEventEmitter)} but using a {@link EventEmitter} rather than a
	 * {@link
	 * GenericEventEmitter}
	 */
	BaseRunnableBuilder<T>.AdditionalEventBuilder on(EventEmitter<?> emitter);

	/**
	 * Execute <b>all</b> defined runnables and bindings on the occurrence of {@code emitter}
	 *
	 * @param emitter the emitter to subscribe to
	 */
	BaseRunnableBuilder<T>.AdditionalEventBuilder on(GenericEventEmitter emitter);

	/**
	 * Adds a listener using {@code listenerAdder} that will execute all previously defined
	 * runnables and bindings.
	 *
	 * @param listenerAdder a method reference to a method such as {@code addClickListener}
	 */
	BaseRunnableBuilder<T>.AdditionalEventBuilder on(Consumer<Runnable> listenerAdder);
}
