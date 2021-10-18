package com.niton.reactj.examples.cli;

import com.niton.reactj.api.binding.builder.CallBuilder;
import com.niton.reactj.api.binding.builder.exposed.ExposedReactiveBinder;
import com.niton.reactj.core.proxy.ProxyCreator;
import com.niton.reactj.core.proxy.ReactiveProxy;


public class CliApp {

	public static void main(String[] args) throws InterruptedException {
		ProxyCreator            creator  = ProxyCreator.besideOrigin();
		ReactiveProxy<Progress> proxy    = creator.create(new Progress());
		Progress                progress = proxy.getObject();

		CliApp app = new CliApp();

		ExposedReactiveBinder binder = new CallBuilder();
		binder.call(app::renderProgress).with(progress::getProgress)
		      .andAlso().call(app::displayDone).when(progress::isDone)
		      .on(proxy.reactEvent());


		while (true) {
			Thread.sleep((long) (Math.random() * 50));
			progress.setProgress((progress.getProgress() + 0.001));
			if (progress.getProgress() >= 1) {
				return;
			}
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

	private void displayDone() {
		System.out.print("✔");
	}

}


