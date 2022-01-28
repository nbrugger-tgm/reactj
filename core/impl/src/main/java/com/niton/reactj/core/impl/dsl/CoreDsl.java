package com.niton.reactj.core.impl.dsl;

import com.niton.reactj.api.binding.dsl.BinderDsl;
import com.niton.reactj.api.binding.dsl.ConsumerDsl;
import com.niton.reactj.api.binding.dsl.RunnableDsl;

import java.util.function.Consumer;

public class CoreDsl implements BinderDsl {
    private boolean recursionPrevention = true;

    @Override
    public RunnableDsl call(Runnable runnable) {
        return new CoreRunnableDsl(runnable, recursionPrevention);
    }

    @Override
    public <T> ConsumerDsl<T> call(Consumer<T> runnable) {
        return new CoreConsumerDsl<>(runnable, recursionPrevention);
    }

    @Override
    public void setRecursionPrevention(boolean preventRecursion) {
        this.recursionPrevention = preventRecursion;
    }
}
