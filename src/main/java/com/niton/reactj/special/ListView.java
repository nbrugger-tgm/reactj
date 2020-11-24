package com.niton.reactj.special;

import com.niton.reactj.ReactiveBinder;
import com.niton.reactj.ReactiveComponent;
import com.niton.reactj.ReactiveController;
import com.niton.reactj.special.ReactiveList;
import com.niton.reactj.special.ReactiveListModel;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * A list view for simple lists like with Strings or INTs
 * @param <M> The class in the list eg. String
 * @param <E> the component class eg. JLabel
 */
public abstract class ListView<M,E> implements ReactiveComponent<Void> {
	private final Function<M,E> elementCreator;
	private final Map<M,E> componentCache = new HashMap<>();
	private final ReactiveController<Void> controller = new ReactiveController<>(this,null);

	protected ListView(Function<M, E> elementCreator) {
		this.elementCreator = elementCreator;
	}

	public abstract void add(int index, E model);
	public abstract void remove(E model);
	public abstract void remove(int index);
	public abstract int size();

	@Override
	public void createBindings(ReactiveBinder binder) {
		ReactiveListModel<M> list = new ReactiveListModel<>(this::convertingAdd,this::remove,(e)-> convertingAdd(size(), e),this::convertingRemove,this::size);
		list.bind(binder);
	}

	private void convertingAdd(int i, M m) {
		E el = elementCreator.apply(m);
		add(i,el);
		componentCache.put(m,el);
	}

	private void convertingRemove(M m) {
		remove(componentCache.get(m));
	}

	@Override
	public void registerListeners(Void controller) {}

	public abstract E getView();

	public void setList(ReactiveList<M> someArray){
		controller.bind(someArray);
	}
}
