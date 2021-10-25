package com.niton.reactj.api.binding.builder.exposed;

import java.util.function.Consumer;

public interface ExposedCallBuilder<
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
	/**
	 * Execute this runnable when the conditions later in the chain apply
	 */
	RB call(Runnable runnable);
	
	/**
	 * Execute this consumer when all conditions in the chain apply
	 */
	<T> ExposedConsumerBuilder<T, CB, RB, COB, CCB, EBB, CEBB, CBB, CSBB, SBB, CRB, BRB> call(Consumer<T> consumer);
}
