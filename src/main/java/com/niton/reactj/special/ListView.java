package com.niton.reactj.special;

import com.niton.reactj.ReactiveBinder;
import com.niton.reactj.ReactiveComponent;
import com.niton.reactj.ReactiveController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * A list view for simple lists like with Strings or INTs
 *
 * @param <M> The class in the list e.g. String
 * @param <E> the component class e.g. JLabel
 * @param <C> The container component class eg. JPanel
 */
public abstract class ListView<M, E, C> implements ReactiveComponent<ReactiveList<M>> {
	private final Function<M, E>                      elementCreator;
	private final Map<M, E>                           componentCache = new ConcurrentHashMap<>();
	private final ReactiveController<ReactiveList<M>> controller;

	protected ListView(Function<M, E> elementCreator) {
		this.elementCreator = elementCreator;
		controller = new ReactiveController<>(this);
	}

	@Override
	public void createBindings(ReactiveBinder<ReactiveList<M>> binder) {
		ReactiveListModel<M> list = new ReactiveListModel<>(
				this::convertingAdd,
				this::remove,
				(e) -> convertingAdd(size(), e),
				this::convertingRemove,
				this::size
		);
		list.bind(binder);
	}

	private void convertingAdd(int index, M element) {
		E convertedElement = elementCreator.apply(element);
		add(index, convertedElement);
		componentCache.put(element, convertedElement);
	}

	public abstract void remove(int index);

	public abstract int size();

	private void convertingRemove(M element) {
		remove(componentCache.get(element));
	}

	public abstract void add(int index, E model);

	public abstract void remove(E model);

	public abstract C getView();

	public void setList(ReactiveList<M> someArray) {
		controller.setModel(someArray);
	}
}
