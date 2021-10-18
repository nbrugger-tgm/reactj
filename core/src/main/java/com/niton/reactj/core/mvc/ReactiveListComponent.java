package com.niton.reactj.core.mvc;

import com.niton.reactj.api.binding.builder.ReactiveBinder;
import com.niton.reactj.api.event.EventEmitter;
import com.niton.reactj.api.mvc.ReactiveComponent;
import com.niton.reactj.lists.diff.ListChange;
import com.niton.reactj.lists.observer.ListObserver;

import java.util.List;

import static com.niton.reactj.api.lists.ListOperation.*;

public abstract class ReactiveListComponent<M, V> extends ReactiveComponent<List<M>, ListChange<M>, V> {
	public final EventEmitter<Integer> onRemove = new EventEmitter<>();
	public final EventEmitter<Integer> onAdd    = new EventEmitter<>();

	protected ReactiveListComponent(ListObserver<M> observer) {
		super(observer);
	}

	@Override
	protected void registerBindings(
			ReactiveBinder<ModelCallBuilder<List<M>>> builder, EventEmitter<ListChange<M>> observerEvent
	) {
		observerEvent.listen(change -> {
			if (change.getOperation() == ADD)
				onAdd.fire(change.getIndex());
			else if (change.getOperation() == REMOVE)
				onRemove.fire(change.getIndex());
		});
		createBindings(builder, onRemove, onAdd, observerEvent);
	}

	protected abstract void createBindings(
			ReactiveBinder<ModelCallBuilder<List<M>>> builder,
			EventEmitter<Integer> onRemove,
			EventEmitter<Integer> onAdd,
			EventEmitter<ListChange<M>> anyListChange
	);
}
