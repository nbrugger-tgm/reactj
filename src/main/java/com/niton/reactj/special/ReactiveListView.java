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

	private void addElement(int i, E e) {
		ReactiveView<?, C, E> view = createElement(e);
		C subV = view.getView();
		viewMap.put(e.getID(), subV);
		addAt(subV, i);
		refresh();
	}

	private void removeFromIndex(int i) {
		removeFrom(i);
		refresh();
	}

	protected abstract int size();

	private void remove(E e) {
		removeById(e.getID());
	}

	public abstract ReactiveView<?, C, E> createElement(E o);

	public abstract void addAt(C subView, int i);

	/**
	 * Some UI frameworks such as swing need to repaint after changes. If your framework doesnt requires such a change you can leave this method empty
	 */
	public abstract void refresh();

	public abstract void removeFrom(int i);

	public void removeById(Object id) {
		remove(viewMap.get(id));
		refresh();
	}

	public abstract void remove(C child);
}
