package com.niton.reactj.special;

import com.niton.reactj.Identity;
import com.niton.reactj.Reactable;
import com.niton.reactj.ReactiveBinder;
import com.niton.reactj.ReactiveView;

import java.util.HashMap;
import java.util.Map;

public abstract class ReactiveListView<L,V,CV,E extends Reactable & Identity<?>> extends ReactiveView<L,V,ReactiveList<E>> {
	private Map<Object,CV> viewMap = new HashMap<>();
	public ReactiveListView(L controller) {
		super(controller);
	}

	@Override
	public void createBindings(ReactiveBinder binder) {
		ReactiveListModel<E> model = new ReactiveListModel<>(this::addElement,this::removeFromIndex,(e)->addElement(size(), e),this::remove,this::size);
		model.bind(binder);
	}

	private void removeFromIndex(int i) {
		removeFrom(i);
		refresh();
	}

	private void remove(E e) {
		removeById(e.getID());
	}

	protected abstract int size();

	public abstract void removeFrom(int i);

	private void addElement(int i, E e) {
		ReactiveView<?,CV,E> view = createElement(e);
		CV subV  = view.getView();
		viewMap.put(e.getID(),subV);
		addAt(subV,i);
		refresh();
	}
	public void removeById(Object id){
		remove(viewMap.get(id));
		refresh();
	}
	public abstract void remove(CV child);
	public abstract ReactiveView<?,CV,E> createElement(E o);
	public abstract void addAt(CV subView,int i);

	/**
	 * Some UI frameworks such as swing need to repaint after changes. If your framework doesnt requires such a change you can leave this method empty
	 */
	public abstract void refresh();
}
