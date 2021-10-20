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
	private final List<Consumer<T>> targets;

	/**
	 * @param targets the initial consumers
	 */
	public ConsumerGroup(Consumer<T>... targets) {this.targets = Arrays.asList(targets);}

	public ConsumerGroup() {
		targets = new ArrayList<>(1);
	}

	@Override
	public void accept(T t) {
		targets.forEach(consumer -> consumer.accept(t));
	}

	public void add(Consumer<T> consumer) {
		targets.add(consumer);
	}
}
