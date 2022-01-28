package com.niton.reactj.core.impl.dsl;

import com.niton.reactj.api.binding.*;
import com.niton.reactj.api.binding.consumer.ConsumerGroup;
import com.niton.reactj.api.binding.consumer.NonCyclicConsumer;
import com.niton.reactj.api.binding.dsl.*;
import com.niton.reactj.api.event.EventEmitter;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class CoreConsumerDsl<N> implements ConvertingConsumerDsl<N>, ConsumerDsl<N> {
    private Consumer<N> consumer;
    private final boolean recursionPrevention;

    public CoreConsumerDsl(Consumer<N> consumer, boolean recursionPrevention) {
        this.consumer = consumer;
        this.recursionPrevention = recursionPrevention;
    }

    @Override
    public ConsumerDsl<N> and(Consumer<? super N> consumer) {
        if (this.consumer instanceof ConsumerGroup) {
            ((ConsumerGroup<N>) this.consumer).add(consumer);
            return this;
        }
        this.consumer = new ConsumerGroup<>(this.consumer, consumer);
        return this;
    }

    @Override
    public <T extends N> ConsumerDsl<T> andCall(Consumer<T> consumer) {
        return new CoreConsumerDsl<>(new ConsumerGroup<>(consumer, this.consumer), recursionPrevention);
    }

    @Override
    public PredicatableDsl<N> on(EventEmitter<? extends N> event) {
        return from(event);
    }

    @Override
    public <F> ConvertingConsumerDsl<F> with(Function<F, N> converter) {
        return from(converter);
    }

    @Override
    public BindingDsl<N> withValue(N constant) {
        return with(new ConstantSupplier<>(constant));
    }

    @Override
    public BindingDsl<N> with(Supplier<? extends N> source) {
        return from(source);
    }

    @Override
    public BindingDsl<N> from(Supplier<? extends N> source) {
        return new CoreBindingDsl<>(new BaseBinding<>(consumer, source), recursionPrevention);
    }

    @Override
    public <S> ConvertingConsumerDsl<S> from(Function<S, ? extends N> converter) {
        return new CoreConsumerDsl<>(v -> consumer.accept(converter.apply(v)), recursionPrevention);
    }

    @Override
    public PredicatableDsl<N> from(EventEmitter<? extends N> event) {
        var conditional = new ConditionalConsumer();
        Consumer<N> listener = conditional;
        if(recursionPrevention)
            listener = new NonCyclicConsumer<>(conditional);

        event.listen(listener::accept);
        return predicate -> {
            conditional.predicate = conditional.predicate.and(predicate);
            return new PredicateDsl<>() {
                @Override
                public PredicateDsl<N> or(Predicate<? super N> value) {
                    conditional.predicate = conditional.predicate.or(predicate);
                    return this;
                }

                @Override
                public PredicateDsl<N> and(Predicate<? super N> value) {
                    conditional.predicate = conditional.predicate.and(predicate);
                    return this;
                }
            };
        };
    }

    /**
     * A conditional wrapper around {@link #consumer}
     */
    private class ConditionalConsumer implements Consumer<N> {
        private Predicate<N> predicate = o -> true;

        @Override
        public void accept(N value) {
            if (predicate.test(value)) consumer.accept(value);
        }
    }
}
