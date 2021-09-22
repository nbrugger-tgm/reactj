package com.niton.reactj.api.examples.superbinding;

import com.niton.reactj.api.proxy.ProxyCreator;
import com.niton.reactj.api.proxy.ProxySubject;
import com.niton.reactj.api.react.ReactiveComponent;
import com.niton.reactj.api.react.ReactiveController;

import static java.lang.String.format;

public class Superbinding {
	public static void main(String[] args) {
		ProxyCreator creator = new ProxyCreator();
		Person       p       = creator.create(new Person("Nils", "Brugger"));

		ReactiveComponent<Person> personCliView = binder -> {
			binder.bind(Person::fullName, s -> System.out.println("fullName(*) -> " + s));
			binder.bind(
					(Person p1) -> p1.getSurname().toUpperCase() + " " + p1.getName(),
					s -> System.out.println("Concat -> " + s)
			);
			binder.bind(
					Person::fullName,
					s -> System.out.println("fullName(unrelated) -> " + s),
					"unrelated"
			);
			binder.bind(
					Person::fullName,
					s -> System.out.println("fullName(name) -> " + s),
					"name"
			);
			binder.bind(
					Person::fullName,
					s -> System.out.println("fullName(name,surename) -> " + s),
					"name",
					"surename"
			);
			binder.bind("name", System.out::println);
			//binder.bind("surename",System.out::println);
		};
		ReactiveController<Person> cont = new ReactiveController<>(personCliView);

		System.out.println("----[Initial Value]----");
		cont.setModel(p);
		System.out.println("----[Change Name]----");
		p.setName("Niton");
		System.out.println("\n----[Change Unrelated]----");
		p.setUnrelated(2);
		System.out.println("\n----[Change Surename]----");
		p.setSurname("Johnson");
	}
}

class Person implements ProxySubject {
	private String name;
	private String surname;
	private int    unrelated;

	public Person(String name, String surename) {
		this.name    = name;
		this.surname = surename;
	}

	public int getUnrelated() {
		return unrelated;
	}

	public void setUnrelated(int unrelated) {
		this.unrelated = unrelated;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String fullName() {
		return format("%s %s", name, surname);
	}
}
