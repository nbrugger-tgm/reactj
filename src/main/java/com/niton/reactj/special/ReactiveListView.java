package com.niton.reactj.special;

import com.niton.reactj.Identity;
import com.niton.reactj.Reactable;
import com.niton.reactj.ReactiveBinder;
import com.niton.reactj.ReactiveView;

import java.util.HashMap;
import java.util.Map;

public abstract class ReactiveListView
		<L, V, C, E extends Reactable & Identity<?>>
		extends ReactiveView<L, V, ReactiveList<E>> {

	private final Map<Object, C> viewMap = new HashMap<>();

	public ReactiveListView(L controller) {
		super(controller);
	}

	@Override
	public void createBindings(ReactiveBinder binder) {
		ReactiveListModel<E> model = new ReactiveListModel<>(
				this::addElement,
				this::removeFromIndex,
				(e) -> addElement(size(), e),
				this::remove,
				this::size
		);
		model.bind(binder);
	}

	private void addElement(int index, E element) {
		ReactiveView<?, C, E> view = createElement(element);
		C subV = view.getView();
		viewMap.put(element.getID(), subV);
		addAt(subV, index);
		refresh();
	}

	private void removeFromIndex(int index) {
		removeFrom(index);
		refresh();
	}

	protected abstract int size();

	private void remove(E element) {
		removeById(element.getID());
	}

	public abstract void remove(C child);

	public abstract ReactiveView<?, C, E> createElement(E element);

	public abstract void addAt(C subView, int index);

	/**
	 * Some UI frameworks such as swing need to repaint after changes. If your framework doesnt requires such a change you can leave this method empty
	 */
	public abstract void refresh();

	public abstract void removeFrom(int index);

	public void removeById(Object id) {
		remove(viewMap.get(id));
		refresh();
	}
}
