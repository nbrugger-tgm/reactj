package com.niton.reactj.examples.swing;

import com.niton.reactj.core.proxy.ProxyCreator;

import javax.swing.*;
import java.awt.*;

public class PersonApp {
	public static void main(String[] args) throws InterruptedException {
		ProxyCreator creator = ProxyCreator.besideOrigin();
		Person       person  = creator.create(new Person(12, "Niton"));

		//A controller for custom events
		PersonController ownController = new PersonController();

		PersonComponent component  = new PersonComponent();
		PersonComponent component1 = new PersonComponent();
		PersonComponent component2 = new PersonComponent();

		component.onReset.listen(ownController);
		component1.onReset.listen(ownController);
		component2.onReset.listen(ownController);

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLayout(new GridLayout());

		frame.add(component.getView());
		frame.add(component1.getView());
		frame.add(component2.getView());

		frame.pack();
		frame.setVisible(true);

		component.setModel(person);
		component1.setModel(person);
		component2.setModel(person);

		person.setAge(19);
		person.setGender(Gender.MALE);
		person.setIq(120);
		person.setName("Niton");

		while (person.getAge() < 100) {
			Thread.sleep(1000);
			System.out.println(person);
			person.setAge(person.getAge() + 1);
		}
	}
}
