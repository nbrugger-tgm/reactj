package com.niton.reactj.api.binding.builder.exposed;

import com.niton.reactj.api.binding.predicates.Condition;

import java.util.function.Predicate;

public interface ExposedConditionalBindingBuilder<
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
		>
		extends ExposedConditionalRunnableBuilder<CB, RB, COB, CCB, EBB, CEBB, CBB, CSBB, SBB, CRB, BRB> {
	ExposedConditionalBindingBuilder<T, CB, RB, COB, CCB, EBB, CEBB, CBB, CSBB, SBB, CRB, BRB> or(Condition condition);

	ExposedConditionalBindingBuilder<T, CB, RB, COB, CCB, EBB, CEBB, CBB, CSBB, SBB, CRB, BRB> and(Condition condition);

	ExposedConditionalBindingBuilder<T, CB, RB, COB, CCB, EBB, CEBB, CBB, CSBB, SBB, CRB, BRB> or(Predicate<T> condition);

	ExposedConditionalBindingBuilder<T, CB, RB, COB, CCB, EBB, CEBB, CBB, CSBB, SBB, CRB, BRB> and(Predicate<T> condition);
}
