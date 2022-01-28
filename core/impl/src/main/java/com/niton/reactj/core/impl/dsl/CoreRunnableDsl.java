package com.niton.reactj.core.impl.dsl;

import com.niton.reactj.api.event.Listenable;
import com.niton.reactj.api.binding.dsl.ConditionalRunnableDsl;
import com.niton.reactj.api.binding.dsl.MultiListenerDsl;
import com.niton.reactj.api.binding.dsl.RunnableDsl;
import com.niton.reactj.api.binding.predicates.Condition;
import com.niton.reactj.api.binding.runnable.NonCyclicRunnable;
import com.niton.reactj.api.binding.runnable.RunnableGroup;

public class CoreRunnableDsl implements RunnableDsl {
    protected final RunnableGroup group = new RunnableGroup();
    private final Runnable exposedRunnable;
    public CoreRunnableDsl(Runnable runnable,boolean cyclicPrevention) {
        group.add(runnable);
        exposedRunnable = cyclicPrevention ? new NonCyclicRunnable(group) : group;
    }

    @Override
    public MultiListenerDsl on(Listenable event) {
        event.listen(exposedRunnable);
        return this::on;
    }

    @Override
    public Runnable build() {
        return exposedRunnable;
    }

    @Override
    public RunnableDsl and(Runnable runnable) {
        group.add(runnable);
        return this;
    }

    @Override
    public ConditionalRunnableDsl when(Condition condition) {
        return new CoreConditionalRunnableDsl(exposedRunnable, condition);
    }
}
