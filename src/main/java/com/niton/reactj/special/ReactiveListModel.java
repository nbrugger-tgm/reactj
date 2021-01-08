package com.niton.reactj.special;

import com.niton.reactj.ReactiveBinder;

import javax.swing.*;
import java.util.List;
import java.util.function.IntSupplier;

import static com.niton.reactj.special.ListActions.*;

/**
 * Reactive model to react to a {@link ReactiveList}
 *
 * @param <E> type of the list
 */
public class ReactiveListModel<E> {
	private final Adder<E>            adder;
	private final Remover<E>          remover;
	private final IndexAdder<E>       intAdder;
	private final IndexRemover        intRemover;
	private final IntSupplier         size;
	private       DefaultListModel<E> swingModel;
	private       int                 index;

	/**
	 * Creates a list model that does supports index based adding
	 */
	public ReactiveListModel(
			IndexAdder<E> intAdder,
			IndexRemover intRemover,
			Adder<E> adder,
			Remover<E> remover,
			IntSupplier size
	) {
		this.adder      = adder;
		this.remover    = remover;
		this.intAdder   = intAdder;
		this.intRemover = intRemover;
		this.size       = size;
	}

	/**
	 * Creates an ListModel that does <b>not</b> supports index based adding and removing
	 *
	 * @param adder   a function to add an object to the ui
	 * @param remover a function to remove an object from the ui
	 * @param size    function to get the number of elements displayed on the ui
	 */
	public ReactiveListModel(Adder<E> adder, Remover<E> remover, IntSupplier size) {
		this.adder      = adder;
		this.remover    = remover;
		this.intAdder   = (i, o) -> adder.add(o);
		this.size       = size;
		this.intRemover = i -> {
			throw new UnsupportedOperationException();
		};
	}

	/**
	 * Creates a swing compatible list model
	 */
	public ReactiveListModel() {
		swingModel = new DefaultListModel<>();
		adder      = swingModel::addElement;
		remover    = swingModel::removeElement;
		intRemover = swingModel::removeElementAt;
		intAdder   = swingModel::add;
		size       = swingModel::size;
	}

	public void bind(ReactiveBinder binder) {
		binder.bind(ADD.id(), adder::add);
		binder.bind(ADD_INDEX.id(), e -> intAdder.add(index, (E) e));
		binder.bind(SET_INDEX.id(), i -> index = (int) i);
		binder.bind(REMOVE_INDEX.id(), intRemover::remove);
		binder.bind(REMOVE_OBJECT.id(), remover::remove);
		binder.bind(REPLACE.id(), o -> {
			intRemover.remove(index);
			intAdder.add(index, (E) o);
		});
		binder.bind(CLEAR.id(),
		            e -> {
			            while (size.getAsInt() > 0) {
				            intRemover.remove(0);
			            }
		            }
		);
		binder.bind(INIT.id(), l -> ((List<E>) l).forEach(adder::add));
	}

	public ListModel<E> swing() {
		return swingModel;
	}

	@FunctionalInterface
	public interface Adder<E> {
		void add(E element);
	}

	@FunctionalInterface
	public interface Remover<E> {
		void remove(E element);
	}

	@FunctionalInterface
	public interface IndexAdder<E> {
		void add(int index, E element);
	}

	@FunctionalInterface
	public interface IndexRemover {
		void remove(int index);
	}
}
