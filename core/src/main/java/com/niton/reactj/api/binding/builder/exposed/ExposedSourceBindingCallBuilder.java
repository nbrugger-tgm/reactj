package com.niton.reactj.api.binding.builder.exposed;

import com.niton.reactj.api.binding.builder.ConditionalSourceBindingBuilder;
import com.niton.reactj.api.binding.builder.SourceBindingCallBuilder;
import com.niton.reactj.api.binding.runnable.RunnableGroup;

public interface ExposedSourceBindingCallBuilder<T>
		extends ExposedBindingBuilder<T, ConditionalSourceBindingBuilder<T>>,
		        ExposedBaseRunnableBuilder<RunnableGroup> {
	/**
	 * Adds a runnable to this group to be executed too
	 */
	SourceBindingCallBuilder<T> and(Runnable runnable);
}
