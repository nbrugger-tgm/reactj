package com.niton.reactj.core.impl.dsl;

import com.niton.reactj.api.binding.Listenable;
import com.niton.reactj.api.binding.dsl.ConditionalRunnableDsl;
import com.niton.reactj.api.binding.dsl.MultiListenerDsl;
import com.niton.reactj.api.binding.predicates.Condition;
import com.niton.reactj.api.binding.runnable.ConditionalRunnable;

public class CoreConditionalRunnableDsl implements ConditionalRunnableDsl {
    private final ConditionalRunnable runnable;

    public CoreConditionalRunnableDsl(
            Runnable runnable,
            Condition condition
    ) {
        this.runnable = new ConditionalRunnable(condition, runnable);
    }

    @Override
    public ConditionalRunnableDsl or(Condition condition) {
        runnable.setCondition(runnable.getCondition().or(condition));
        return this;
    }

    @Override
    public ConditionalRunnableDsl and(Condition condition) {
        runnable.setCondition(runnable.getCondition().and(condition));
        return this;
    }

    @Override
    public Runnable build() {
        return runnable;
    }

    @Override
    public MultiListenerDsl on(Listenable event) {
        event.listen(runnable);
        return this::on;
    }
}
