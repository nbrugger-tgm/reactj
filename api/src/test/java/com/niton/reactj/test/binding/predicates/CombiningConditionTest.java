package com.niton.reactj.test.binding.predicates;

import com.niton.reactj.api.binding.predicates.CombiningCondition;
import com.niton.reactj.api.binding.predicates.Condition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CombiningConditionTest {
	@Test
	void and() {
		var and = new CombiningCondition.And(Condition.YES, Condition.NO);
		assertFalse(and.check());
		and = new CombiningCondition.And(Condition.NO, Condition.NO);
		assertFalse(and.check());
		and = new CombiningCondition.And(Condition.NO, Condition.YES);
		assertFalse(and.check());
		and = new CombiningCondition.And(Condition.YES, Condition.YES);
		assertTrue(and.check());
	}

	@Test
	void or() {
		var and = new CombiningCondition.Or(Condition.YES, Condition.NO);
		assertTrue(and.check());
		and = new CombiningCondition.Or(Condition.NO, Condition.NO);
		assertFalse(and.check());
		and = new CombiningCondition.Or(Condition.NO, Condition.YES);
		assertTrue(and.check());
		and = new CombiningCondition.Or(Condition.YES, Condition.YES);
		assertTrue(and.check());
	}

}