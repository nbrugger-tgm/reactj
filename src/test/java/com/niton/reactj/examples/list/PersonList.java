package com.niton.reactj.examples.list;

import com.niton.reactj.examples.swing.Gender;
import com.niton.reactj.special.ReactiveListView;

import javax.swing.*;

import static javax.swing.BoxLayout.Y_AXIS;

public class PersonList extends ReactiveListView<JPanel, JPanel, Person> {
	private JButton addButton;
	private JPanel      entryPanel;
	private JScrollPane pane;

	@Override
	protected void registerListeners() {
		addButton.addActionListener(e -> getData().add(new Person(12, "Max Musterman")));
	}

	@Override
	protected int size() {
		return entryPanel.getComponentCount();
	}

	@Override
	public void removeFrom(int index) {
		entryPanel.remove(index);
	}

	@Override
	public void remove(JPanel child) {
		entryPanel.remove(child);
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
		view.removeEvent.listen(getData()::remove);
		return view;
	}

	@Override
	public void addAt(JPanel subView, int index) {
		entryPanel.add(subView, index);
	}

	@Override
	public void refresh() {
		//Just ..... swing
		entryPanel.validate();
		pane.repaint();
	}

	@Override
	protected JPanel createView() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, Y_AXIS));
		pane = new JScrollPane();

		addButton    = new JButton("Add new Person");
		panel.add(addButton);

		entryPanel = new JPanel();
		entryPanel.setLayout(new BoxLayout(entryPanel, Y_AXIS));
		pane.setViewportView(entryPanel);

		panel.add(pane);

		return panel;
	}
}
