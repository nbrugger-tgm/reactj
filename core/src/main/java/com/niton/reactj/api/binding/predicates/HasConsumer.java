package com.niton.reactj.api.binding.predicates;

import java.util.function.Consumer;

public interface HasConsumer<T> {
	Consumer<T> getConsumer();
}
