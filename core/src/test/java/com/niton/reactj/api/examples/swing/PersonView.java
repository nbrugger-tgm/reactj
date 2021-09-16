package com.niton.reactj.api.examples.swing;

import com.niton.reactj.api.ReactiveBinder;
import com.niton.reactj.api.ReactiveProxy;
import com.niton.reactj.api.annotation.Reactive;
import com.niton.reactj.api.mvc.ReactiveView;
import com.niton.reactj.event.EventManager;

import javax.swing.*;
import java.awt.*;

//For swing components we recommend the com.niton.reactj:swing implementation
public class PersonView extends ReactiveView<JPanel, ReactiveProxy<Person>> {

	public final EventManager<Person> resetEvent = new EventManager<>();
	private      JPanel               panel;
	private      JTextField           surnameInput;
	private      JTextField           ageInput;
	private      JTextField           iqField;
	private      JComboBox<Gender>    genderJComboBox;
	private      JButton              selectButton;

	@Override
	protected JPanel createView() {
		panel           = new JPanel();
		surnameInput    = new JTextField();
		ageInput        = new JTextField();
		iqField         = new JTextField();
		genderJComboBox = new JComboBox<>(Gender.values());
		selectButton    = new JButton("Reset");


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
	public void createBindings(ReactiveBinder<ReactiveProxy<Person>> binder) {
		binder.bindBi("surename", surnameInput::setText, surnameInput::getText);
		surnameInput.getDocument().addUndoableEditListener(binder::react);
		//surnameInput.addActionListener(bindings::react);

		//bind with value conversion
		//UI cannot change IQ
		binder.bind("iq", iqField::setText, String::valueOf);

		binder.bindBi("gender", genderJComboBox::setSelectedItem, genderJComboBox::getSelectedItem);
		genderJComboBox.addActionListener(binder::react);

		//react to changes in many and different ways
		binder.bind("gender", this::adaptColorToGender);

		//bidirectional binding (With value conversion)
		binder.bindBi("age",
		              ageInput::setText,
		              ageInput::getText,
		              Integer::parseInt,
		              String::valueOf);
		ageInput.addActionListener(binder::react);
	}


	public void adaptColorToGender(Gender g) {
		Color c = Color.WHITE;
		if(g == Gender.MALE)
			c = Color.BLUE;
		if(g == Gender.FEMALE)
			c = Color.PINK;
		panel.setBackground(c);
	}

	@Override
	protected void registerListeners() {
		selectButton.addActionListener(e -> resetEvent.fire(getController().getModel()
		                                                                   .getObject()));
	}

	@Reactive("age")
	public void adaptSizeToAge(int age) {
		System.out.println("adapt font size for age of " + age + " years");
	}

}
