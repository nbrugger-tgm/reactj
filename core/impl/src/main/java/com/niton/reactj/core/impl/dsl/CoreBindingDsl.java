package com.niton.reactj.core.impl.dsl;

import com.niton.reactj.api.binding.Binding;
import com.niton.reactj.api.binding.ConditionalBinding;
import com.niton.reactj.api.binding.NonCyclicBinding;
import com.niton.reactj.api.binding.dsl.BindingDsl;
import com.niton.reactj.api.binding.dsl.ConditionalBindingDsl;
import com.niton.reactj.api.binding.predicates.Condition;

import java.util.function.Predicate;

public class CoreBindingDsl<T> extends CoreRunnableDsl implements BindingDsl<T> {
    private final Binding<T> binding;
    private final boolean    recursionPrevention;

    public CoreBindingDsl(Binding<T> binding, boolean recursionPrevention) {
        super(binding, recursionPrevention);
        this.binding             = binding;
        this.recursionPrevention = recursionPrevention;
    }

    @Override
    public ConditionalBindingDsl<T> when(Condition condition) {
        return when(condition.toPredicate());
    }

    @Override
    public ConditionalBindingDsl<T> when(Predicate<? super T> predicate) {
        //prevent double execution of binding (from within the group & by itself)
        group.remove(binding);
        //This bundle is to execute the added runnables together with the binding
        var bundled            = new BundleBinding<>(binding, build());
        var wrapped            = recursionPrevention ? new NonCyclicBinding<>(bundled) : bundled;
        var conditionalBinding = new ConditionalBinding<>(wrapped, predicate);
        return new CoreConditionalBindingDsl<>(conditionalBinding);
    }

    /**
     * A bundle is a group of runnables that are executed together with a binding
     *
     * @param <T> the type of the binding
     */
    private static class BundleBinding<T> implements Binding<T> {
        private final Binding<T> binding;
        private final Runnable   runnable;

        public BundleBinding(
                Binding<T> binding,
                Runnable group
        ) {
            this.binding  = binding;
            this.runnable = group;
        }

        @Override
        public void accept(T t) {
            runnable.run();
            binding.accept(t);
        }

        @Override
        public T get() {
            return binding.get();
        }
    }

}
