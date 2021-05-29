package com.niton.reactj.examples.superbinding;

import com.niton.reactj.*;
import com.niton.reactj.mvc.ReactiveView;

public class Superbinding {
	public static void main(String[] args) {
		Person p = ReactiveProxy.create(Person.class,"Nils","Brugger");
		ReactiveComponent<Person> personCliView = new ReactiveComponent<Person>() {
			@Override
			public void createBindings(ReactiveBinder<Person> binder) {
				binder.bind(Person::fullName,s->System.out.println("fullName(*) -> "+s));
				binder.bind((Person p)->p.getSurename().toUpperCase()+" "+p.getName(),s->System.out.println("Concat -> "+s));
				binder.bind(Person::fullName,s->System.out.println("fullName(unrelated) -> "+s),"unrelated");
				binder.bind(Person::fullName,s->System.out.println("fullName(name) -> "+s),"name");
				binder.bind(Person::fullName,s->System.out.println("fullName(name,surename) -> "+s),"name","surename");
				binder.bind("name",System.out::println);
				//binder.bind("surename",System.out::println);
			}
		};
		ReactiveController<Person> cont = new ReactiveController<>(personCliView);
		cont.bind(p);
		cont.update();
		p.setName("Niton");
		p.setUnrelated(2);
		p.setSurename("Johnson");
	}
}
class Person implements ProxySubject {
	private String name,surename;
	private int unrelated;

	public void setUnrelated(int unrelated) {
		this.unrelated = unrelated;
	}

	public int getUnrelated() {
		return unrelated;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSurename(String surename) {
		this.surename = surename;
	}

	public String getName() {
		return name;
	}

	public String getSurename() {
		return surename;
	}

	public String fullName(){
		return String.format("%s %s",name,surename);
	}

	public Person(String name, String surename) {
		this.name     = name;
		this.surename = surename;
	}
}
