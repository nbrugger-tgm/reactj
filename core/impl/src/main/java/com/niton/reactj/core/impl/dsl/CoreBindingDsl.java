package com.niton.reactj.core.impl.dsl;

import com.niton.reactj.api.binding.Binding;
import com.niton.reactj.api.binding.ConditionalBinding;
import com.niton.reactj.api.binding.dsl.BindingDsl;
import com.niton.reactj.api.binding.dsl.ConditionalBindingDsl;
import com.niton.reactj.api.binding.predicates.Condition;
import com.niton.reactj.api.binding.runnable.RunnableGroup;

import java.util.function.Predicate;

public class CoreBindingDsl<T> extends CoreRunnableDsl implements BindingDsl<T> {
    private final Binding<T> binding;

    private static class BundleBinding<T> extends Binding<T> {
        public BundleBinding(
                Binding<T> binding,
                RunnableGroup group
        ) {
            super(v -> {
                binding.getConsumer().accept(v);
                group.run();
            }, binding.getSource());
        }
    }

    public CoreBindingDsl(Binding<T> binding) {
        super(binding);
        this.binding = binding;
    }

    @Override
    public ConditionalBindingDsl<T> when(Condition condition) {
        return when(condition.toPredicate());
    }

    @Override
    public ConditionalBindingDsl<T> when(Predicate<? super T> predicate) {
        //prevent double execution of binding (from within the group & by itself)
        group.remove(binding);
        //This bundle is to execute the added Runnables together with the binding
        var bundled            = new BundleBinding<>(binding, group);
        var conditionalBinding = new ConditionalBinding<>(bundled, predicate);
        return new CoreConditionalBindingDsl<>(conditionalBinding);
    }
}
