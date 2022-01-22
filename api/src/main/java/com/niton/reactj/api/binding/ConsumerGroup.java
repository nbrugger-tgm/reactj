package com.niton.reactj.api.binding;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A group of multiple consumers of the same type. <b>mutable</b>
 *
 * @param <T> the type of the consumers
 */
public class ConsumerGroup<T> implements Consumer<T> {
	private final Set<Consumer<? super T>> targets = new HashSet<>();

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
	public void accept(T t) {
		targets.forEach(consumer -> consumer.accept(t));
	}
}
