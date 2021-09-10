package com.niton.reactj.examples.CLI;

import com.niton.reactj.*;
import com.niton.reactj.annotation.Reactive;

import static com.niton.reactj.examples.CLI.CliApp.Progress;

public class CliApp {

	public static void main(String[] args) throws InterruptedException {
		ReactiveProxy<Progress> proxy    = ReactiveObject.createProxy(Progress.class);
		Progress                progress = proxy.getObject();

		ReactiveController<ReactiveProxy<Progress>> controller = new ReactiveController<>(new ProgressCli());
		controller.setModel(proxy);

		while(true) {
			Thread.sleep((long) (Math.random() * 70));
			progress.setProgress((progress.getProgress() + 0.001));
			if(progress.getProgress() >= 1) {
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

	@Override
	public void createBindings(ReactiveBinder<ReactiveProxy<Progress>> binder) {
		binder.bind("percent", this::renderProgress);
		binder.<Double>showIf("percent", this::displayNearlyDone, p -> p >= 0.8);
	}

	private void displayNearlyDone(Boolean condition) {
		if(condition) {
			System.out.print("  !Nearly done!");
		}
	}

	private void renderProgress(double percent) {
		int width = 70;
		int done  = (int) (percent * width);
		System.out.print("\r");
		System.out.print('[');
		for(int i = 0; i < width; i++) {
			System.out.print(i < done ? "█" : (i == done ? '>' : ' '));
		}
		System.out.print(']');
		System.out.print((int) (percent * 100) + "%");
	}
}