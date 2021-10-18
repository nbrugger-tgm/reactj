package com.niton.reactj.core.mvc;

import com.niton.reactj.api.binding.builder.CallBuilder;
import com.niton.reactj.api.binding.builder.ConsumerBuilder;

import java.util.function.Supplier;

public class ModelCallBuilder<M> extends CallBuilder {
	private final Supplier<M> modelRef;

	@FunctionalInterface
	public interface Setter<M, V> {
		void set(M object, V value);
	}

	public ModelCallBuilder(Supplier<M> model) {this.modelRef = model;}

	public <T> ConsumerBuilder<T> call(Setter<M, T> setter) {
		return super.call(val -> setter.set(modelRef.get(), val));
	}
}
