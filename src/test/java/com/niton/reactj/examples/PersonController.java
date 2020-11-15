package com.niton.reactj.examples;

import java.awt.event.ActionEvent;

public class PersonController {
	private Person information;

	public PersonController(Person information) {
		this.information = information;
	}

	public void reset(ActionEvent actionEvent) {
		information.setAge(19);
		information.setName("Niton");
		information.setIq(0);
		information.setGender(Gender.OTHER);
	}
}
