package com.niton.reactj.examples.list;

import com.niton.reactj.examples.swing.Gender;
import com.niton.reactj.special.ReactiveListView;

import javax.swing.*;

import static javax.swing.BoxLayout.Y_AXIS;

public class PersonList extends ReactiveListView<JPanel, JPanel, Person> {
	private JButton addButton;
	private JButton removeButton;
	private JPanel  list;

	@Override
	protected void registerListeners() {
		removeButton.addActionListener(e -> getModel().remove(0));
		addButton.addActionListener(e -> getModel().add(new Person(12, "Max Musterman")));
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
	public PersonView createElement(Person element) {
		PersonView view = new PersonView(element);
		view.resetEvent.listen(p -> {
			p.setAge(18);
			p.setName("Max Mustermann");
			p.setIq(99);
			p.setGender(Gender.MALE);
		});
		view.removeEvent.listen(getModel()::remove);
		return view;
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

		addButton    = new JButton("Add");
		removeButton = new JButton("Remove");
		panel.add(addButton);
		panel.add(removeButton);

		list = new JPanel();
		list.setLayout(new BoxLayout(list, Y_AXIS));
		pane.setViewportView(list);

		panel.add(pane);

		return panel;
	}
}
