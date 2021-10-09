package com.niton.reactj.api.test.models;

import com.niton.reactj.core.annotation.Unreactive;

public class Base {
	private int a;
	@Unreactive
	private int b;

	public int getB() {
		return b;
	}

	public void setB(int b) {
		this.b = b;
	}

	public int getA() {
		return a;
	}

	public void setA(int a) {
		this.a = a;
	}
}
