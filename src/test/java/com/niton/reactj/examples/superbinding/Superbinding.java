package com.niton.reactj.examples.superbinding;

import com.niton.reactj.ReactiveComponent;
import com.niton.reactj.ReactiveController;
import com.niton.reactj.proxy.ProxyCreator;
import com.niton.reactj.proxy.ProxySubject;

public class Superbinding {
	public static void main(String[] args) {
		Person p = ProxyCreator.subject(Person.class, "Nils", "Brugger");

		ReactiveComponent<Person> personCliView = binder -> {
			binder.bind(Person::fullName, s -> System.out.println("fullName(*) -> " + s));
			binder.bind((Person p1) -> p1.getSurename().toUpperCase() + " " + p1.getName(),
			            s -> System.out.println("Concat -> " + s));
			binder.bind(Person::fullName,
			            s -> System.out.println("fullName(unrelated) -> " + s),
			            "unrelated");
			binder.bind(Person::fullName,
			            s -> System.out.println("fullName(name) -> " + s),
			            "name");
			binder.bind(Person::fullName,
			            s -> System.out.println("fullName(name,surename) -> " + s),
			            "name",
			            "surename");
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
		p.setSurename("Johnson");
	}
}

class Person implements ProxySubject {
	private String name, surename;
	private int unrelated;

	public Person(String name, String surename) {
		this.name     = name;
		this.surename = surename;
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

	public String getSurename() {
		return surename;
	}

	public void setSurename(String surename) {
		this.surename = surename;
	}

	public String fullName() {
		return String.format("%s %s", name, surename);
	}
}
