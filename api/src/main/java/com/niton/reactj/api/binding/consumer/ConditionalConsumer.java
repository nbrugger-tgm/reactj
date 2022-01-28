package com.niton.reactj.api.binding.consumer;

import com.niton.reactj.api.binding.predicates.HasPredicate;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ConditionalConsumer<T> implements Consumer<T>, HasPredicate<T> {
    private final Consumer<? super T>  consumer;
    private       Predicate<? super T> condition;

    public ConditionalConsumer(Consumer<? super T> consumer, Predicate<? super T> condition) {
        this.consumer  = consumer;
        this.condition = condition;
    }

    @Override
    public void accept(T t) {
        if (getPredicate().test(t)) {
            consumer.accept(t);
        }
    }

    @Override
    public Predicate<T> getPredicate() {
        return condition::test;
    }

    @Override
    public void setPredicate(Predicate<? super T> predicate) {
        this.condition = predicate;
    }
}
