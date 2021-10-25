package com.niton.reactj.implementation.binding;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModelCallBuilder<M> extends CallBuilder {
	private final Supplier<M> modelRef;

	@FunctionalInterface
	public interface Setter<M, V> {
		void set(M object, V value);
	}

	public ModelCallBuilder(Supplier<M> model) {this.modelRef = model;}

	public <T> ConsumerBuilder<T, ModelCallBuilder<M>> call(Setter<M, T> setter) {
		return super.call(val -> setter.set(modelRef.get(), val));
	}

	@Override
	public <T> ConsumerBuilder<T, ModelCallBuilder<M>> call(Consumer<T> runnable) {
		return super.call(runnable);
	}

	public RunnableBuilder invoke(Consumer<M> method) {
		return super.call(() -> method.accept(modelRef.get()));
	}
}
