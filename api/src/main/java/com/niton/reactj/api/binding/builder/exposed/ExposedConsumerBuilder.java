package com.niton.reactj.api.binding.builder.exposed;

import com.niton.reactj.api.binding.predicates.ConstantSupplier;
import com.niton.reactj.api.event.EventEmitter;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ExposedConsumerBuilder<
		T,
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
	ExposedEventBindingBuilder<T, CB, RB, COB, CCB, EBB, CEBB, CBB, CSBB, SBB, CRB, BRB> on(EventEmitter<T> event);

	<C> ExposedConvertingConsumerBuilder<C, CB, RB, COB, CCB, EBB, CEBB, CBB, CSBB, SBB, CRB, BRB> with(Function<C, T> converter);

	ExposedConsumerBuilder<T, CB, RB, COB, CCB, EBB, CEBB, CBB, CSBB, SBB, CRB, BRB> and(Consumer<T> consumer);

	default ExposedSourceBindingBuilder<T, CB, RB, COB, CCB, EBB, CEBB, CBB, CSBB, SBB, CRB, BRB> with(T value) {
		return with(new ConstantSupplier<>(value));
	}

	ExposedSourceBindingBuilder<T, CB, RB, COB, CCB, EBB, CEBB, CBB, CSBB, SBB, CRB, BRB> with(Supplier<T> source);
}
