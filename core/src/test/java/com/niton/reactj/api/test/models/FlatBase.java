package com.niton.reactj.api.test.models;

import com.niton.reactj.core.annotation.ReactiveResolution;

import static com.niton.reactj.core.annotation.ReactiveResolution.ReactiveResolutionType.FLAT;

@ReactiveResolution(FLAT)
public class FlatBase extends Base {
	private int c;

	public int getC() {
		return c;
	}

	public void setC(int c) {
		this.c = c;
	}
}
