package com.niton.reactj.examples;

import com.niton.reactj.ReactiveObject;
import com.niton.reactj.ReactiveProxy;

import javax.swing.*;

public class PersonApp {
	public static void main(String[] args) throws InterruptedException {
		ReactiveProxy<Person> proxy = ReactiveObject.create(Person.class, 12, "Niton");

		Person information = proxy.object;
		PersonController controller = new PersonController(information);
		PersonView component = new PersonView(controller);

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.add(component.getView());
		frame.pack();
		frame.setVisible(true);

		component.setData(proxy.reactive);

		information.setAge(19);
		information.setGender(Gender.MALE);
		information.setIq(120);
		information.setName("Niton");

		for (; ; ) {
			Thread.sleep(1000);
			System.out.println(information);
			information.setAge(information.getAge() + 1);
		}
	}
}
