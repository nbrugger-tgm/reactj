package com.niton.reactj.api.binding.builder;

import com.niton.reactj.api.event.EventEmitter;
import com.niton.reactj.utils.event.GenericEventEmitter;

public class BaseRunnableBuilder<T extends Runnable> {

	protected final BindingBuilder rootBuilder;
	protected final T              runnable;

	public class AdditionalEventBuilder {
		public AdditionalEventBuilder andOn(GenericEventEmitter emitter) {
			emitter.listen(rootBuilder.getTarget()::run);
			return this;
		}
	}

	public BaseRunnableBuilder(T r, BindingBuilder rootBuilder) {
		this.runnable    = r;
		this.rootBuilder = rootBuilder;
	}

	/**
	 * Append a new execution statement to this binding
	 */
	public ExposedBindingBuilder andAlso() {
		rootBuilder.add(runnable);
		return rootBuilder;
	}

	/**
	 * Same as {@link #on(GenericEventEmitter)} but using a {@link EventEmitter} rather than a {@link
	 * GenericEventEmitter}
	 */
	public AdditionalEventBuilder on(EventEmitter<?> emitter) {
		rootBuilder.add(runnable);
		emitter.addListener(ignored -> rootBuilder.getTarget().run());
		return new AdditionalEventBuilder();
	}

	/**
	 * Execute <b>all</b> defined runnables and bindings on the occurrence of {@code emitter}
	 *
	 * @param emitter the emitter to subscribe to
	 */
	public AdditionalEventBuilder on(GenericEventEmitter emitter) {
		rootBuilder.add(runnable);
		emitter.addListener(rootBuilder.getTarget()::run);
		return new AdditionalEventBuilder();
	}

}
