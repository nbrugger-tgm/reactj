package com.niton.reactj.api.test.models;

import com.niton.reactj.core.annotation.ReactiveResolution;

import static com.niton.reactj.core.annotation.ReactiveResolution.ReactiveResolutionType.DEEP;

@ReactiveResolution(DEEP)
public class DeepBase extends Base {
	private int c;

	public int getC() {
		return c;
	}

	public void setC(int c) {
		this.c = c;
	}
}
