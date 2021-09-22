package com.niton.reactj.api.examples.cli;

import com.niton.reactj.api.annotation.Reactive;
import com.niton.reactj.api.examples.cli.CliApp.Progress;
import com.niton.reactj.api.proxy.ProxyCreator;
import com.niton.reactj.api.react.ReactiveBinder;
import com.niton.reactj.api.react.ReactiveComponent;
import com.niton.reactj.api.react.ReactiveController;
import com.niton.reactj.api.react.ReactiveProxy;


public class CliApp {

	public static void main(String[] args) throws InterruptedException {
		ProxyCreator            creator  = new ProxyCreator();
		ReactiveProxy<Progress> proxy    = creator.create(new Progress());
		Progress                progress = proxy.getObject();

		ReactiveController<ReactiveProxy<Progress>> controller = new ReactiveController<>(new ProgressCli());
		controller.setModel(proxy);

		while (true) {
			Thread.sleep((long) (Math.random() * 50));
			progress.setProgress((progress.getProgress() + 0.001));
			if (progress.getProgress() >= 1) {
				return;
			}
		}
	}

	public static class Progress {
		@Reactive("percent")
		private double progress;

		public double getProgress() {
			return progress;
		}

		public void setProgress(double progress) {
			this.progress = progress;
		}
	}
}


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