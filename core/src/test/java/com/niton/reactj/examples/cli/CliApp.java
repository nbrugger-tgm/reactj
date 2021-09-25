package com.niton.reactj.examples.cli;

import com.niton.reactj.core.proxy.ProxyCreator;
import com.niton.reactj.core.react.ReactiveController;
import com.niton.reactj.core.react.ReactiveProxy;


public class CliApp {

	public static void main(String[] args) throws InterruptedException {
		ProxyCreator creator = new ProxyCreator();
		ReactiveProxy<Progress> proxy = creator.create(new Progress());
		Progress progress = proxy.getObject();

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

}


