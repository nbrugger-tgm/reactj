package com.niton.reactj.api.binding.builder;

import com.niton.reactj.api.binding.builder.exposed.ExposedBaseRunnableBuilder;
import com.niton.reactj.api.binding.builder.exposed.ExposedReactiveBinder;
import com.niton.reactj.utils.event.EventEmitter;
import com.niton.reactj.utils.event.GenericEventEmitter;

import java.util.function.Consumer;

public class BaseRunnableBuilder<T extends Runnable> implements ExposedBaseRunnableBuilder<T> {

	protected final CallBuilder rootBuilder;
	protected final T           runnable;

	public interface OneArgumentEventAdder<T> extends Consumer<Consumer<T>> {
	}

	public class AdditionalEventBuilder {
		public AdditionalEventBuilder andOn(GenericEventEmitter emitter) {
			emitter.listen(rootBuilder.getTarget()::run);
			return this;
		}
	}

	public BaseRunnableBuilder(T r, CallBuilder rootBuilder) {
		this.runnable    = r;
		this.rootBuilder = rootBuilder;
	}

	@Override
	public ExposedReactiveBinder andAlso() {
		rootBuilder.add(runnable);
		return rootBuilder;
	}

	@Override
	public AdditionalEventBuilder on(EventEmitter<?> emitter) {
		rootBuilder.add(runnable);
		emitter.addListener(ignored -> rootBuilder.getTarget().run());
		return new AdditionalEventBuilder();
	}

	@Override
	public AdditionalEventBuilder on(GenericEventEmitter emitter) {
		rootBuilder.add(runnable);
		emitter.addListener(rootBuilder.getTarget()::run);
		return new AdditionalEventBuilder();
	}

	@Override
	public AdditionalEventBuilder on(Consumer<Runnable> listenerAdder) {
		rootBuilder.add(runnable);
		listenerAdder.accept(rootBuilder.getTarget());
		return new AdditionalEventBuilder();
	}
}
