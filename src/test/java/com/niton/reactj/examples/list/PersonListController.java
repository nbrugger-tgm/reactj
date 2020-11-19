package com.niton.reactj.examples.list;

import java.awt.event.ActionEvent;
import java.util.List;

public class PersonListController {
	private final List<Person> list;

	public PersonListController(List<Person> list) {
		this.list = list;
	}

	public void add(ActionEvent actionEvent) {
		list.add(new Person(0, "Max Mustermann"));
	}

	public void remove(int selectedIndex) {
		list.remove(selectedIndex);
	}
}
