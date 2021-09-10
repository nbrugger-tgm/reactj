package com.niton.reactj.special;

import com.niton.reactj.Reactable;
import com.niton.reactj.ReactiveBinder;
import com.niton.reactj.mvc.ReactiveView;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A class for List with reactive Objects in it
 *
 * @param <V> The component used as view (eg. JPanel/JList)
 * @param <C> Entry component type (eg JPanel/JLabel)
 * @param <E> the data type of the list (eg. Person)
 */
public abstract class ReactiveListView
	<V, C, E extends Reactable & Identity<?>>
	extends ReactiveView<V, ReactiveList<E>> {

	private final Map<Object, C> viewMap = new ConcurrentHashMap<>();


	@Override
	public void createBindings(ReactiveBinder<ReactiveList<E>> binder) {
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
		ReactiveView<C, E> view = createElement(element);
		C                  subV = view.getView();
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

	public abstract ReactiveView<C, E> createElement(E element);

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
