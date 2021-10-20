package com.niton.reactj.api.binding.builder.exposed;

import com.niton.reactj.api.binding.builder.SourceBindingBuilder;
import com.niton.reactj.api.binding.builder.conditional.ConditionalSourceBindingBuilder;
import com.niton.reactj.api.binding.runnable.RunnableGroup;

public interface ExposedSourceBindingCallBuilder<T>
		extends ExposedBindingBuilder<T, ConditionalSourceBindingBuilder<T>>,
		        ExposedBaseRunnableBuilder<RunnableGroup> {
	/**
	 * Adds a runnable to this group to be executed too
	 */
	SourceBindingBuilder<T> and(Runnable runnable);
}
