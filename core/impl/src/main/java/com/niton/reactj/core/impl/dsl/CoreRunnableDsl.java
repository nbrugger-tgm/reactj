package com.niton.reactj.core.impl.dsl;

import com.niton.reactj.api.binding.Listenable;
import com.niton.reactj.api.binding.dsl.ConditionalRunnableDsl;
import com.niton.reactj.api.binding.dsl.MultiListenerDsl;
import com.niton.reactj.api.binding.dsl.RunnableDsl;
import com.niton.reactj.api.binding.predicates.Condition;
import com.niton.reactj.api.binding.runnable.RunnableGroup;

public class CoreRunnableDsl implements RunnableDsl {
    protected final RunnableGroup group = new RunnableGroup();

    public CoreRunnableDsl(Runnable runnable) {
        group.add(runnable);
    }

    @Override
    public MultiListenerDsl on(Listenable event) {
        event.listen(group);
        return this::on;
    }

    @Override
    public Runnable build() {
        return group;
    }

    @Override
    public RunnableDsl and(Runnable runnable) {
        group.add(runnable);
        return this;
    }

    @Override
    public ConditionalRunnableDsl when(Condition condition) {
        return new CoreConditionalRunnableDsl(group, condition);
    }
}
