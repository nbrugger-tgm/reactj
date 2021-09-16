package com.niton.reactj.api.examples.swing;


import com.niton.reactj.event.Listener;

public class PersonController implements Listener<Person> {
	@Override
	public void onAction(Person event) {
		event.setAge(19);
		event.setGender(Gender.MALE);
		event.setName("NitonFx");
		event.setIq(95);
	}
}
