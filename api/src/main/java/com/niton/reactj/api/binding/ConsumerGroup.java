package com.niton.reactj.api.binding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * A group of multiple consumers of the same type. <b>mutable</b>
 *
 * @param <T> the type of the consumers
 */
public class ConsumerGroup<T> implements Consumer<T> {
    private final List<Consumer<? super T>> targets = new ArrayList<>();

    /**
     * @param targets the initial consumers
     */
    @SafeVarargs
    public ConsumerGroup(Consumer<? super T>... targets) {
        add(targets);
    }

    @SafeVarargs
    public final void add(Consumer<? super T>... targets) {
        this.targets.addAll(Arrays.asList(targets));
    }

    public ConsumerGroup() {
    }

    @Override
    public void accept(T value) {
        targets.forEach(consumer -> consumer.accept(value));
    }
}
