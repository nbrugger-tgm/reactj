package com.niton.reactj.examples.list;

import com.niton.reactj.ReactiveView;
import com.niton.reactj.special.ReactiveListView;

import javax.swing.*;

import static javax.swing.BoxLayout.Y_AXIS;

public class PersonList extends ReactiveListView<PersonListController, JPanel,JPanel, Person> {
	private JButton                   addButton;
	private JButton                   removeButton;
	private JPanel                    list;

	public PersonList(PersonListController controller) {
		super(controller);
	}

	@Override
	protected int size() {
		return list.getComponentCount();
	}

	@Override
	public void removeFrom(int index) {
		list.remove(index);
	}

	@Override
	public void remove(JPanel child) {
		list.remove(child);
	}

	@Override
	public ReactiveView<?, JPanel, Person> createElement(Person element) {
		return new PersonView(element);
	}

	@Override
	public void addAt(JPanel subView, int index) {
		list.add(subView, index);
	}

	@Override
	public void refresh() {
		//Just ..... swing
		list.validate();
		list.repaint();
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



	@Override
	public void registerListeners(PersonListController controller) {
		addButton.addActionListener(controller::add);
		removeButton.addActionListener((e) -> controller.remove(0));
	}
}
