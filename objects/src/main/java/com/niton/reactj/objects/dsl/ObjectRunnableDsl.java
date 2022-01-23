package com.niton.reactj.objects.dsl;

import com.niton.reactj.api.binding.dsl.RunnableDsl;
import com.niton.reactj.api.binding.predicates.Condition;

public interface ObjectRunnableDsl extends ObjectListenerDsl, RunnableDsl {
    @Override
    ObjectRunnableDsl and(Runnable runnable);

    @Override
    ObjectConditionalRunnableDsl when(Condition condition);
}
