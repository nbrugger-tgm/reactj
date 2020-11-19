package com.niton.reactj.special;

import com.niton.reactj.ReactiveBinder;

import javax.swing.*;
import java.util.List;
import java.util.function.IntSupplier;

import static com.niton.reactj.special.ReactiveList.*;

/**
 * Reactive model to react to a {@link ReactiveList}
 * @param <E> type of the list
 */
public class ReactiveListModel<E> {
	private Adder<E>      adder;
	private Remover<E>    remover;
	private IndexAdder<E> intAdder;
	private IndexRemover  intRemover;
	private IntSupplier   size;

	/**
	 * Creates a list model that does supports index based adding
	 */
	public ReactiveListModel(IndexAdder<E> intAdder, IndexRemover intRemover, Adder<E> adder, Remover<E> remover, IntSupplier size) {
		this.adder = adder;
		this.remover = remover;
		this.intAdder = intAdder;
		this.intRemover = intRemover;
		this.size = size;
	}

	private DefaultListModel<E> swingModel = null;

	/**
	 * Creates an ListModel that does <b>not</b> supports index based adding and removing
	 * @param adder
	 * @param remover
	 * @param size
	 */
	public ReactiveListModel(Adder<E> adder, Remover<E> remover, IntSupplier size) {
		this.adder = adder;
		this.remover = remover;
		this.intAdder = (i,o) -> adder.add(o);
		this.size = size;
		this.intRemover = i -> {throw new UnsupportedOperationException();};
	}

	/**
	 * Creates a swing compatible list model
	 */
	public ReactiveListModel() {
		swingModel = new DefaultListModel<>();
		adder = swingModel::addElement;
		remover = swingModel::removeElement;
		intRemover = swingModel::removeElementAt;
		intAdder = swingModel::add;
		size = swingModel::size;
	}
	private int index;
	public void bind(ReactiveBinder binder){
		binder.bind(ADD, adder::add);
		binder.bind(ADD_INDEX,e -> intAdder.add(index, (E) e));
		binder.bind(SET_INDEX, i -> index = (int) i);
		binder.bind(REMOVE_INDEX,intRemover::remove);
		binder.bind(REMOVE_OBJECT,remover::remove);
		binder.bind(CLEAR,e -> {while (size.getAsInt()>0)intRemover.remove(0);});
		binder.bind(INIT,l -> ((List<E>)l).forEach(adder::add));
	}

	public ListModel<E> swing(){
		return swingModel;
	}
	@FunctionalInterface
	public interface Adder<E> {
		public void add(E element);
	}

	@FunctionalInterface
	public interface Remover<E> {
		public void remove(E element);
	}

	@FunctionalInterface
	public interface IndexAdder<E> {
		public void add(int i,E element);
	}

	@FunctionalInterface
	public interface IndexRemover {
		public void remove(int i);
	}
}
