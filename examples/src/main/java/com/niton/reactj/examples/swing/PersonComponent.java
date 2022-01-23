package com.niton.reactj.examples.swing;

import com.niton.reactj.api.event.EventEmitter;
import com.niton.reactj.api.event.GenericEventEmitter;
import com.niton.reactj.objects.ReactiveObjectComponent;
import com.niton.reactj.objects.annotations.ReactiveListener;
import com.niton.reactj.objects.dsl.ObjectDsl;
import com.niton.reactj.objects.observer.PropertyObservation;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.function.Function;

//For swing components we recommend the com.niton.reactj:swing implementation
public class PersonComponent extends ReactiveObjectComponent<Person, JPanel> {

	public final  EventEmitter<Person> onReset            = new EventEmitter<>();
	private final GenericEventEmitter  surnameFieldChange = new GenericEventEmitter();
	private final GenericEventEmitter  changeGender       = new GenericEventEmitter();
	private final GenericEventEmitter  ageChange          = new GenericEventEmitter();
	private       JPanel               panel;
	private       JTextField           surnameInput;
	private       JTextField           ageInput;
	private       JTextField           iqField;
	private       JComboBox<Gender>    genderJComboBox;
	private       JButton              selectButton;


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
	protected void createBindings(
			ObjectDsl<Person> binder,
			EventEmitter<Person> onModelChange,
			EventEmitter<PropertyObservation<Person>> onPropertyChange
	) {

		Function<String, Integer> parseInt = Integer::parseInt;
		binder.call(surnameInput::setText)
		      .with(Person::getName)
		      .from(onModelChange);
		binder.call(iqField::setText)
		      .with(String::valueOf)
		      .from(Person::getIq)
		      .from(onModelChange)
		      .when(Objects::nonNull);
		binder.call(genderJComboBox::setSelectedItem)
		      .with(Person::getGender)
		      .from(onModelChange);


		binder.call(Person::setName)
		      .with(surnameInput::getText)
		      .on(surnameFieldChange);
		surnameInput.getDocument().addUndoableEditListener(surnameFieldChange::fire);
		//surnameInput.addActionListener(bindings::react);

		binder.call(Person::setGender)
		      .withCasted(genderJComboBox::getSelectedItem)
		      .on(changeGender);
		genderJComboBox.addActionListener(changeGender::fire);

		//react to changes in many and different ways
		binder.call(this::adaptColorToGender)
		      .with(Person::getGender)
		      .from(onModelChange);

		//bidirectional binding (With value conversion)
		binder.call(ageInput::setText)
		      .with(Object::toString)
		      .from(Person::getAge)
		      .from(onModelChange);

		binder.call(Person::setAge)
		      .with(parseInt)
		      .from(ageInput::getText)
		      .on(ageChange);

		ageInput.addActionListener(ageChange::fire);
	}

	public void adaptColorToGender(Gender g) {
		Color c = Color.WHITE;
		if (g == Gender.MALE)
			c = Color.BLUE;
		if (g == Gender.FEMALE)
			c = Color.PINK;
		panel.setBackground(c);
	}

	@ReactiveListener("age")
	public void adaptSizeToAge(int age) {
		System.out.println("adapt font size for age of " + age + " years");
	}
}