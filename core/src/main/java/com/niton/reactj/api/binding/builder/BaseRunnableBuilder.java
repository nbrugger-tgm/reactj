package com.niton.reactj.api.binding.builder;

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

	public RunnableCallBuilder and(Runnable runnable) {
		return rootBuilder.call(runnable);
	}

	public ExposedBindingBuilder andAlso() {
		return rootBuilder;
	}

	public AdditionalEventBuilder on(GenericEventEmitter emitter) {
		emitter.addListener(rootBuilder.getTarget()::run);
		return new AdditionalEventBuilder();
	}
}
