package com.niton.reactj.examples.swing;

import com.niton.reactj.objects.annotations.ReactiveResolution;
import com.niton.reactj.objects.annotations.Unreactive;
import com.niton.reactj.objects.proxy.ProxySubject;

import static com.niton.reactj.objects.annotations.ReactiveResolution.ReactiveResolutionType.FLAT;

//optional
@ReactiveResolution(FLAT)
public class Person implements ProxySubject {
    private int    age;
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
        return "PersonalInformation{" + "age=" + age +
                ", name='" + name + '\'' +
                ", iq=" + iq +
                ", gender=" + gender +
                ", address='" + address + '\'' +
                '}';
    }
}
