package com.niton.reactj.special;

import com.niton.reactj.mvc.ReactiveBinder;
import com.niton.reactj.ReactiveComponent;
import com.niton.reactj.mvc.ReactiveController;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * A list view for simple lists like with Strings or INTs
 *
 * @param <M> The class in the list eg. String
 * @param <E> the component class eg. JLabel
 * @param <C> The container component class eg. JPanel
 */
public abstract class ListView<M, E, C> implements ReactiveComponent<Void> {
	private final Function<M, E>                            elementCreator;
	private final Map<M, E>                                 componentCache = new HashMap<>();
	private final ReactiveController<Void, ReactiveList<M>> controller;

	protected ListView(Function<M, E> elementCreator) {
		this.elementCreator = elementCreator;
		controller          = new ReactiveController<>(this, null);
	}

	@Override
	public void createBindings(ReactiveBinder binder) {
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
		E el = elementCreator.apply(element);
		add(index, el);
		componentCache.put(element, el);
	}

	public abstract void remove(int index);

	public abstract void remove(E model);

	public abstract int size();

	private void convertingRemove(M element) {
		remove(componentCache.get(element));
	}

	public abstract void add(int index, E model);


	@Override
	public void registerListeners(Void controller) {
		//Simple lists are immutable therefore there are no listeners
	}

	public abstract C getView();

	public void setList(ReactiveList<M> someArray) {
		controller.bind(someArray);
	}
}
