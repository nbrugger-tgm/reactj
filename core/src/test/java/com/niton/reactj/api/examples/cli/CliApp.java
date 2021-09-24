package com.niton.reactj.api.examples.cli;

import com.niton.reactj.api.proxy.ProxyCreator;
import com.niton.reactj.api.react.ReactiveController;
import com.niton.reactj.api.react.ReactiveProxy;


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


