package com.niton.reactj.examples.list;

import com.niton.reactj.examples.swing.Gender;

import java.awt.event.ActionEvent;

public class PersonController {
	private final Person information;

	public PersonController(Person information) {
		this.information = information;
	}

	public void reset(ActionEvent actionEvent) {
		information.setAge(18);
		information.setName("Max Mustermann");
		information.setIq(99);
		information.setGender(Gender.MALE);
	}
}
