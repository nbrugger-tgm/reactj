package com.niton.reactj.examples.cli;

import com.niton.reactj.core.proxy.ProxyCreator;
import com.niton.reactj.core.proxy.ReactiveProxy;
import com.niton.reactj.core.react.ReactiveController;


public class CliApp {

	public static void main(String[] args) throws InterruptedException {
		ProxyCreator creator = ProxyCreator.besideOrigin();
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


