package com.niton.reactj.examples;

import com.niton.reactj.ReactiveController;

import javax.swing.*;

public class PersonalInformationApp {
	public static void main(String[] args) throws InterruptedException {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		PersonalInformation information = new PersonalInformation();
		PersonalInformationComponent component = new PersonalInformationComponent();
		PersonalInformationController controller = new PersonalInformationController(information);
		ReactiveController<PersonalInformationController> reactor = new ReactiveController<>(component,controller);

		frame.add(component);
		frame.pack();
		frame.setVisible(true);

		reactor.bind(information);

		information.setAge(19);
		information.setGender(Gender.MALE);
		information.setIq(120);
		information.setName("Niton");


		for (;;){
			Thread.sleep(2000);
			System.out.println(information);
			information.setAge(information.getAge()+1);
		}
	}
}
