package com.niton.reactj.examples.swing;

import com.niton.reactj.annotation.ReactivResolution;
import com.niton.reactj.annotation.Reactive;
import com.niton.reactj.annotation.Unreactive;

import static com.niton.reactj.annotation.ReactivResolution.ReactiveResolutions.FLAT;

//optional
@ReactivResolution(FLAT)
public class Person {
	private int    age;
	//change reactive name
	@Reactive("surename")
	private String name;
	private int    iq;
	private Gender gender;
	//This will not be reacted to
	@Unreactive
	private String address;

	public Person(int age, String name) {
		this.age  = age;
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIq() {
		return iq;
	}

	public void setIq(int iq) {
		this.iq = iq;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("PersonalInformation{");
		sb.append("age=").append(age);
		sb.append(", name='").append(name).append('\'');
		sb.append(", iq=").append(iq);
		sb.append(", gender=").append(gender);
		sb.append(", address='").append(address).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
