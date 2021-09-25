package com.niton.reactj.examples.cli;

import com.niton.reactj.api.mvc.ReactiveComponent;
import com.niton.reactj.core.proxy.ReactiveProxy;
import com.niton.reactj.core.react.ReactiveBinder;

class ProgressCli implements ReactiveComponent<ReactiveProxy<Progress>> {


	@Override
	public void createBindings(ReactiveBinder<ReactiveProxy<Progress>> binder) {
		binder.bind("percent", this::renderProgress);
		binder.<Double>showIf("percent", this::displayDone, p -> p >= 0.999);
	}

	private void renderProgress(double percent) {
		int width = 50;
		double done = (percent * width);
		double port = done % 1.0;
		int fullDone = (int) (done - port);
		System.out.print("\r");
		System.out.print('[');
		for (int i = 0; i < width; i++) {
			System.out.print(i < fullDone ? "█" : (i == fullDone ? '>' : ' '));
		}
		System.out.print(']');
		System.out.print((int) (percent * 100) + "%");
	}

	private void displayDone(Boolean condition) {
		if (condition) {
			System.out.print("✔");
		}
	}
}
