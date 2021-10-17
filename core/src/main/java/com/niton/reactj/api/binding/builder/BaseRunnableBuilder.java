package com.niton.reactj.api.binding.builder;

import com.niton.reactj.api.event.EventEmitter;
import com.niton.reactj.utils.event.GenericEventEmitter;

public class BaseRunnableBuilder {

	protected final BindingBuilder rootBuilder;
	protected final Runnable       runnable;

	public class AdditionalEventBuilder {
		public AdditionalEventBuilder andOn(GenericEventEmitter emitter) {
			emitter.listen(rootBuilder.getTarget()::run);
			return this;
		}
	}

	public BaseRunnableBuilder(Runnable r, BindingBuilder rootBuilder) {
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

	public AdditionalEventBuilder on(EventEmitter<?> emitter) {
		rootBuilder.add(runnable);
		emitter.addListener(ignored -> rootBuilder.getTarget().run());
		return new AdditionalEventBuilder();
	}

	public AdditionalEventBuilder on(GenericEventEmitter emitter) {
		rootBuilder.add(runnable);
		emitter.addListener(rootBuilder.getTarget()::run);
		return new AdditionalEventBuilder();
	}

}
