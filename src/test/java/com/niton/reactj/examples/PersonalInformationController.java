package com.niton.reactj.examples;

import java.awt.event.ActionEvent;

public class PersonalInformationController {
	private PersonalInformation information;

	public PersonalInformationController(PersonalInformation information) {
		this.information = information;
	}

	public void personalInformationSubmitted(ActionEvent actionEvent) {
		information.setAge(19);
		information.setName("Niton");
		information.setIq(0);
		information.setGender(Gender.OTHER);
	}
}
