package com.niton.reactj.api.binding.builder.exposed;

import com.niton.reactj.api.event.EventEmitter;
import com.niton.reactj.api.event.GenericEventEmitter;

import java.util.function.Consumer;

public interface ExposedBaseRunnableBuilder<
		CB extends ExposedCallBuilder<CB, RB, COB, CCB, EBB, CEBB, CBB, CSBB, SBB, CRB, BRB>,
		RB extends ExposedRunnableBuilder<CB, RB, COB, CCB, EBB, CEBB, CBB, CSBB, SBB, CRB, BRB>,
		COB extends ExposedConsumerBuilder<?, CB, RB, COB, CCB, EBB, CEBB, CBB, CSBB, SBB, CRB, BRB>,
		CCB extends ExposedConvertingConsumerBuilder<?, CB, RB, COB, CCB, EBB, CEBB, CBB, CSBB, SBB, CRB, BRB>,
		EBB extends ExposedEventBindingBuilder<?, CB, RB, COB, CCB, EBB, CEBB, CBB, CSBB, SBB, CRB, BRB>,
		CEBB extends ExposedConditionalEventBindingBuilder<?, CB, RB, COB, CCB, EBB, CEBB, CBB, CSBB, SBB, CRB, BRB>,
		CBB extends ExposedConditionalBindingBuilder<?, CB, RB, COB, CCB, EBB, CEBB, CBB, CSBB, SBB, CRB, BRB>,
		CSBB extends ExposedConditionalSourceBindingBuilder<?, CB, RB, COB, CCB, EBB, CEBB, CBB, CSBB, SBB, CRB, BRB>,
		SBB extends ExposedSourceBindingBuilder<?, CB, RB, COB, CCB, EBB, CEBB, CBB, CSBB, SBB, CRB, BRB>,
		CRB extends ExposedConditionalRunnableBuilder<CB, RB, COB, CCB, EBB, CEBB, CBB, CSBB, SBB, CRB, BRB>,
		BRB extends ExposedBaseRunnableBuilder<CB, RB, COB, CCB, EBB, CEBB, CBB, CSBB, SBB, CRB, BRB>
		> {
	interface Additional {

		/**
		 * Same as {@link #on(GenericEventEmitter)} but using a {@link EventEmitter} rather than a
		 * {@link
		 * GenericEventEmitter}
		 */
		Additional andOn(EventEmitter<?> emitter);

		/**
		 * Execute <b>all</b> defined runnables and bindings on the occurrence of {@code emitter}
		 *
		 * @param emitter the emitter to subscribe to
		 */
		Additional andOn(GenericEventEmitter emitter);

		/**
		 * Adds a listener using {@code listenerAdder} that will execute all previously defined
		 * runnables and bindings.
		 *
		 * @param listenerAdder a method reference to a method such as {@code addClickListener}
		 */
		Additional andOn(Consumer<Runnable> listenerAdder);
	}

	/**
	 * Append a new execution statement to this binding
	 */
	CB andAlso();

	/**
	 * Same as {@link #on(GenericEventEmitter)} but using a {@link EventEmitter} rather than a
	 * {@link
	 * GenericEventEmitter}
	 */
	Additional on(EventEmitter<?> emitter);

	/**
	 * Execute <b>all</b> defined runnables and bindings on the occurrence of {@code emitter}
	 *
	 * @param emitter the emitter to subscribe to
	 */
	Additional on(GenericEventEmitter emitter);

	/**
	 * Adds a listener using {@code listenerAdder} that will execute all previously defined
	 * runnables and bindings.
	 *
	 * @param listenerAdder a method reference to a method such as {@code addClickListener}
	 */
	Additional on(Consumer<Runnable> listenerAdder);
}
