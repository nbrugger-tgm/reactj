package com.niton.reactj.lists.mvc;

import com.niton.reactj.api.event.EventEmitter;
import com.niton.reactj.api.mvc.ReactiveComponent;
import com.niton.reactj.implementation.binding.ModelCallBuilder;
import com.niton.reactj.implementation.binding.ReactiveBinder;
import com.niton.reactj.lists.diff.ListChange;
import com.niton.reactj.lists.observer.ListObserver;

import java.util.List;

import static com.niton.reactj.lists.diff.ListOperation.ADD;
import static com.niton.reactj.lists.diff.ListOperation.REMOVE;

public abstract class ReactiveListComponent<M, V>
		extends ReactiveComponent<List<M>, ListChange<M>, V> {
	public final EventEmitter<Integer> onRemove = new EventEmitter<>();
	public final EventEmitter<Integer> onAdd    = new EventEmitter<>();

	protected ReactiveListComponent() {
		super(new ListObserver<>());
	}

	@Override
	protected void registerBindings(
			EventEmitter<ListChange<M>> observerEvent
	) {
		observerEvent.listen(change -> {
			if (change.getOperation() == ADD)
				onAdd.fire(change.getIndex());
			else if (change.getOperation() == REMOVE)
				onRemove.fire(change.getIndex());
		});
		createBindings(new ReactiveBinder<>(() -> new ModelCallBuilder<>(this::getModel)),
		               onRemove,
		               onAdd,
		               observerEvent
		);
	}

	protected abstract void createBindings(
			ReactiveBinder<ModelCallBuilder<List<M>>> builder,
			EventEmitter<Integer> onRemove,
			EventEmitter<Integer> onAdd,
			EventEmitter<ListChange<M>> anyListChange
	);
}
