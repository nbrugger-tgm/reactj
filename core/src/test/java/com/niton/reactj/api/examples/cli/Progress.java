package com.niton.reactj.api.examples.cli;

import com.niton.reactj.api.annotation.Reactive;

public class Progress {
	@Reactive("percent")
	private double progress;

	public double getProgress() {
		return progress;
	}

	public void setProgress(double progress) {
		this.progress = progress;
	}
}
