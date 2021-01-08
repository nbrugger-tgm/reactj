package com.niton.reactj.examples.swing;

import com.niton.reactj.mvc.Listener;

import java.awt.event.ActionEvent;

public class PersonController implements Listener<Person> {
	@Override
	public void onAction(Person event) {
		event.setAge(19);
		event.setGender(Gender.MALE);
		event.setName("NitonFx");
		event.setIq(95);
	}
}
