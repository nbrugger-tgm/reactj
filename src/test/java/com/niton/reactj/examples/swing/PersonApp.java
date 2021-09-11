package com.niton.reactj.examples.swing;

import com.niton.reactj.ReactiveObject;
import com.niton.reactj.proxy.ReactiveProxy;
import com.niton.reactj.ReactiveStrategy;

import javax.swing.*;
import java.awt.*;

public class PersonApp {
	public static void main(String[] args) throws InterruptedException {
		ReactiveProxy<Person> proxy = ReactiveObject.createProxy(Person.class, 12, "Niton");
		proxy.setStrategy(ReactiveStrategy.REACT_ON_ALL);

		//A controller for custom events
		PersonController ownController = new PersonController();

		Person     information = proxy.getObject();
		PersonView component   = new PersonView();
		PersonView component1  = new PersonView();
		PersonView component2  = new PersonView();

		component.resetEvent.listen(ownController);
		component1.resetEvent.listen(ownController);
		component2.resetEvent.listen(ownController);

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

		while(true) {
			Thread.sleep(1000);
			System.out.println(information);
			information.setAge(information.getAge() + 1);
		}
	}
}
