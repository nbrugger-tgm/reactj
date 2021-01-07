package com.niton.reactj.examples.list;

import com.niton.reactj.mvc.ReactiveBinder;
import com.niton.reactj.ReactiveView;
import com.niton.reactj.annotation.Reactive;
import com.niton.reactj.examples.swing.Gender;

import javax.swing.*;
import java.awt.*;

public class PersonView extends ReactiveView<PersonController, JPanel, Person> {

	private JPanel panel;

	private JTextField        surnameInput;
	private JTextField        ageInput;
	private JTextField        iqField;
	private JComboBox<Gender> genderJComboBox;
	private JButton           selectButton;

	public PersonView(Person person) {
		super(new PersonController(person));
		setData(person);
	}


	@Override
	protected JPanel createView() {
		panel = new JPanel();
		surnameInput = new JTextField();
		ageInput = new JTextField();
		iqField = new JTextField();
		genderJComboBox = new JComboBox<>(Gender.values());
		selectButton = new JButton("Reset");


		panel.add(surnameInput);
		surnameInput.setColumns(10);

		panel.add(ageInput);
		ageInput.setColumns(10);

		panel.add(iqField);
		iqField.setColumns(10);

		panel.add(genderJComboBox);

		panel.add(selectButton);

		return panel;
	}

	@Override
	public void createBindings(ReactiveBinder binder) {
		binder.bindBi("surename", surnameInput::setText, surnameInput::getText);
		surnameInput.getDocument().addUndoableEditListener(binder::react);
		//surnameInput.addActionListener(bindings::react);

		//bind with value conversion
		//UI cannot change IQ
		binder.bind("iq", iqField::setText, String::valueOf);

		binder.bindBi("gender", genderJComboBox::setSelectedItem, genderJComboBox::getSelectedItem);
		genderJComboBox.addActionListener(binder::react);


		//bidirectional binding (With value conversion)
		binder.bindBi("age", ageInput::setText, ageInput::getText, Integer::parseInt, String::valueOf);
		ageInput.addActionListener(binder::react);
	}

	@Override
	public void registerListeners(PersonController controller) {
		selectButton.addActionListener(controller::reset);
	}

	@Reactive("gender")
	public void adaptColorToGender(Gender g) {
		panel.setBackground(g == Gender.MALE ? Color.BLUE : (g == Gender.FEMALE ? Color.PINK : Color.WHITE));
		panel.repaint();
	}

}
