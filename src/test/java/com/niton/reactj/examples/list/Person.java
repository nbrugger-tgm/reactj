package com.niton.reactj.examples.list;

import com.niton.reactj.special.Identity;
import com.niton.reactj.ReactiveObject;
import com.niton.reactj.annotation.ReactivResolution;
import com.niton.reactj.annotation.Reactive;
import com.niton.reactj.annotation.Unreactive;
import com.niton.reactj.examples.swing.Gender;

import java.util.Objects;

import static com.niton.reactj.annotation.ReactivResolution.ReactiveResolutions.FLAT;

//optional
@ReactivResolution(FLAT)
public class Person extends ReactiveObject implements Identity<String> {
	public static int ID = 0;
	private final int id = ID++;
	private int    age;
	//change reactive name
	@Reactive("surename")
	private String name;
	private int    iq;
	private Gender gender = Gender.OTHER;
	//This will not be reacted to
	@Unreactive
	private String address;

	public Person(int age, String name) {
		this.age = age;
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
		react();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		react();
	}

	public int getIq() {
		return iq;
	}

	public void setIq(int iq) {
		this.iq = iq;
		react();
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
		react();
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

	@Override
	public String getID() {
		return name+address;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Person)) return false;
		Person person = (Person) o;
		return age == person.age && iq == person.iq && name.equals(person.name) && gender == person.gender && Objects
				.equals(address, person.address) && Objects.equals(id,person.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(age, name, iq, gender, address,id);
	}
}
