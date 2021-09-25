package com.niton.reactj.examples.cli;

import com.niton.reactj.core.annotation.Reactive;

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
