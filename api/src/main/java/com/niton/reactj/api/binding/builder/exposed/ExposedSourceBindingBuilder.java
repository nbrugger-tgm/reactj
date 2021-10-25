package com.niton.reactj.api.binding.builder.exposed;

import com.niton.reactj.api.binding.predicates.Condition;

import java.util.function.Predicate;

public interface ExposedSourceBindingBuilder<
		T, CB extends ExposedCallBuilder<CB, RB, COB, CCB, EBB, CEBB, CBB, CSBB, SBB, CRB, BRB>,
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
		extends ExposedBaseRunnableBuilder<CB, RB, COB, CCB, EBB, CEBB, CBB, CSBB, SBB, CRB, BRB> {
	ExposedConditionalSourceBindingBuilder<T, CB, RB, COB, CCB, EBB, CEBB, CBB, CSBB, SBB, CRB, BRB> when(Condition condition);

	ExposedConditionalSourceBindingBuilder<T, CB, RB, COB, CCB, EBB, CEBB, CBB, CSBB, SBB, CRB, BRB> when(Predicate<T> condition);
}
