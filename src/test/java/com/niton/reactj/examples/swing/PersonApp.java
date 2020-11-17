package com.niton.reactj.examples.swing;

import com.niton.reactj.ReactiveObject;
import com.niton.reactj.ReactiveProxy;
import com.niton.reactj.ReactiveStrategy;

import javax.swing.*;
import java.awt.*;

public class PersonApp {
	public static void main(String[] args) throws InterruptedException {
		ReactiveProxy<Person> proxy = ReactiveObject.create(Person.class, 12, "Niton");

		Person information = proxy.object;
		proxy.reactive.setStrategy(ReactiveStrategy.REACT_ON_ALL);
		PersonController controller = new PersonController(information);
		PersonView component = new PersonView(controller);
		PersonView component1 = new PersonView(controller);
		PersonView component2 = new PersonView(controller);

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLayout(new GridLayout());
		frame.add(component.getView());
		frame.add(component1.getView());
		frame.add(component2.getView());
		frame.pack();
		frame.setVisible(true);

		component.setData(proxy);
		component1.setData(proxy);
		component2.setData(proxy);

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
