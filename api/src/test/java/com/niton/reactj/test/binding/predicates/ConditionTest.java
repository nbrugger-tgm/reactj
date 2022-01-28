package com.niton.reactj.test.binding.predicates;

import com.niton.reactj.api.binding.predicates.Condition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConditionTest {

    @Test
    void combiner() {
        assertTrue(Condition.YES.and(Condition.YES).check());
        assertTrue(Condition.NO.or(Condition.YES).check());
        assertTrue(Condition.YES.or(Condition.NO).check());
        assertTrue(Condition.NO.not().check());
        assertTrue(Condition.YES.toPredicate().test(null));
        assertTrue(Condition.YES.toPredicate().test(new Object()));


        assertFalse(Condition.NO.or(Condition.NO).check());
        assertFalse(Condition.NO.and(Condition.YES).check());
        assertFalse(Condition.YES.and(Condition.NO).check());
        assertFalse(Condition.YES.and(Condition.NO).check());
        assertFalse(Condition.YES.not().check());
        assertFalse(Condition.NO.toPredicate().test(null));
        assertFalse(Condition.NO.toPredicate().test(new Object()));
    }
}