package com.niton.reactj.examples.CLI;

import com.niton.reactj.*;
import com.niton.reactj.annotation.Reactive;
import com.niton.reactj.mvc.ReactiveBinder;
import com.niton.reactj.mvc.ReactiveController;
import com.niton.reactj.mvc.ReactiveModel;
import com.niton.reactj.mvc.ReactiveObject;

public class CliApp {

	public static void main(String[] args) throws InterruptedException {
		ReactiveProxy<Progress> proxy = ReactiveObject.create(Progress.class);
		Progress progress = proxy.object;

		ReactiveController<CliController,ReactiveModel<Progress>> controller;
		controller = new ReactiveController<>(new ProgressCli(), null);
		controller.bind(proxy.reactive);

		while (true) {
			Thread.sleep((long) (Math.random()*90));
			progress.setProgress((progress.getProgress() + 0.002) % 1);
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


class ProgressCli implements ReactiveComponent<CliController> {

	@Override
	public void createBindings(ReactiveBinder binder) {
		binder.bind("percent", this::renderProgress);
		binder.<Double>showIf("percent",this::displayNearlyDone,p -> p>=0.8);
	}

	private void displayNearlyDone(Boolean condition) {
		if(condition)
			System.out.print("  !Nearly done!");
	}

	private void renderProgress(double percent) {
		int width = 30;
		int done = (int) (percent * width);
		System.out.print("\r");
		System.out.print('[');
		for (int i = 0; i < width; i++) {
			System.out.print(i < done ? "█" : (i == done ? '>' : ' '));
		}
		System.out.print(']');
		System.out.print((int) (percent * 100) + "%");
	}

	@Override
	public void registerListeners(CliController controller) {
	}
}

class CliController {

}