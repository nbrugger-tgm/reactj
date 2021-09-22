package com.niton.reactj.api.examples.cli;

import com.niton.reactj.api.react.ReactiveBinder;
import com.niton.reactj.api.react.ReactiveComponent;
import com.niton.reactj.api.react.ReactiveProxy;

class ProgressCli implements ReactiveComponent<ReactiveProxy<Progress>> {


	private void displayDone(Boolean condition) {
		if (condition) {
			System.out.print("✔");
		}
	}

	private void renderProgress(double percent) {
		int    width    = 50;
		double done     = (percent * width);
		double port     = done % 1.0;
		int    fullDone = (int) (done - port);
		System.out.print("\r");
		System.out.print('[');
		for (int i = 0; i < width; i++) {
			System.out.print(i < fullDone ? "█" : (i == fullDone ? '>' : ' '));
		}
		System.out.print(']');
		System.out.print((int) (percent * 100) + "%");
	}

	@Override
	public void createBindings(ReactiveBinder<ReactiveProxy<Progress>> binder) {
		binder.bind("percent", this::renderProgress);
		binder.<Double>showIf("percent", this::displayDone, p -> p >= 0.999);
	}
}