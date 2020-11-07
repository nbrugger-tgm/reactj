package com.niton.reactj.examples;

import com.niton.reactj.annotation.Reactive;
import com.niton.reactj.ReactiveBinder;
import com.niton.reactj.ReactiveComponent;

import javax.swing.*;

public class PersonalInformationComponent extends JPanel implements ReactiveComponent<PersonalInformationController> {

	private JTextField surnameInput;

	private JTextField ageInput;

	private JComboBox<Gender> genderJComboBox;

	private JTextField iqField;

	private JButton selectButton;



	public PersonalInformationComponent() {
		surnameInput = new JTextField();
		add(surnameInput);
		surnameInput.setColumns(10);

		ageInput = new JTextField();
		add(ageInput);
		ageInput.setColumns(10);

		iqField = new JTextField();
		add(iqField);
		iqField.setColumns(10);
		
		genderJComboBox = new JComboBox<>(Gender.values());
		add(genderJComboBox);
		
		selectButton = new JButton("Reset");
		add(selectButton);
	}

	@Override
	public void createBindings(ReactiveBinder bindings){
		//just display values
		bindings.bindEdit("surename",surnameInput::setText,surnameInput::getText);
		//surnameInput.getDocument().addUndoableEditListener(bindings::updateModel);
		surnameInput.addActionListener(bindings::react);

		//bind with value conversion
		bindings.bind("iq",iqField::setText,String::valueOf);

		//react to changes in many and different ways
		bindings.bind("gender",genderJComboBox::setSelectedItem);
		bindings.bind("gender",this::adaptColorToGender);

		//bidirectional binding (With value conversion
		bindings.bindEdit("age",ageInput::setText,ageInput::getText,Integer::parseInt, String::valueOf);
		ageInput.addActionListener(bindings::react);
	}

	@Override
	public void registerListeners(PersonalInformationController controller){
		selectButton.addActionListener(controller::personalInformationSubmitted);
	}
	public void adaptColorToGender(Gender g){
		System.out.println("Adapt color for "+g);
	}

	@Reactive("age")
	public void adaptSizeToAge(int age){
		System.out.println("adapt font size for age of "+age+" years");
	}
}
