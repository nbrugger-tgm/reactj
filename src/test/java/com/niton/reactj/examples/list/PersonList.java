package com.niton.reactj.examples.list;

import com.niton.reactj.ReactiveBinder;
import com.niton.reactj.ReactiveView;
import com.niton.reactj.special.ReactiveList;
import com.niton.reactj.special.ReactiveListModel;

import javax.swing.*;

import static javax.swing.BoxLayout.Y_AXIS;

public class PersonList extends ReactiveView<PersonListController, JPanel, ReactiveList<Person>> {
	private ReactiveListModel<Person> model;
	private JButton                   addButton;
	private JButton                   removeButton;
	private JPanel                    list;

	public PersonList(PersonListController controller) {
		super(controller);
	}

	@Override
	protected JPanel createView() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, Y_AXIS));
		JScrollPane pane = new JScrollPane();

		addButton = new JButton("Add");
		removeButton = new JButton("Remove");
		panel.add(addButton);
		panel.add(removeButton);

		list = new JPanel();
		list.setLayout(new BoxLayout(list, Y_AXIS));
		pane.setViewportView(list);

		panel.add(pane);


		return panel;
	}
	private int i = 0;
	@Override
	public void createBindings(ReactiveBinder binder) {
		model = new ReactiveListModel<>(this::indexAdd, this::indexRemove, this::add, this::remove, list::getComponentCount);
		model.bind(binder);
		binder.bindBi("penis",System.out::println,()->"penis"+i++);
		addButton.addActionListener(binder::react);
	}

	private void indexAdd(int i, Person o) {
		list.add(new PersonView(o).getView(), i);
	}

	private void indexRemove(int i) {
		list.remove(i);
		list.validate();
	}

	private void add(Person o) {
		list.add(new PersonView(o).getView());
		list.validate();
	}

	private void remove(Person o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void registerListeners(PersonListController controller) {
		addButton.addActionListener(controller::add);
		removeButton.addActionListener((e) -> controller.remove(0));
	}
}
