package com.niton.reactj.examples.CLI;

import com.niton.reactj.*;
import com.niton.reactj.annotation.Reactive;

public class CliApp {

	public static void main(String[] args) throws InterruptedException {
		ReactiveProxy<Progress> proxy = ReactiveObject.create(Progress.class);
		Progress progress = proxy.object;
		ReactiveController<CliController> controller = new ReactiveController<>(new ProgressCli(),null);
		controller.bind(proxy.reactive);

		while (true){
			Thread.sleep(85);
			progress.setProgress((progress.getProgress()+0.01)%1);
		}
	}
}
class Progress {
	@Reactive("percent")
	private double progress;

	public double getProgress() {
		return progress;
	}

	public void setProgress(double progress) {
		this.progress = progress;
	}
	public Progress(){}
}
class ProgressCli implements ReactiveComponent<CliController> {

	private void renderProgress(double percent) {
		int width = 30;
		int done = (int) (percent*width);
		System.out.print("\r");
		System.out.print('[');
		for (int i = 0; i < width; i++) {
			System.out.print(i<=done ? '=' : '-');
		}
		System.out.print(']');
		System.out.print((int)(percent*100)+"%");
	}

	@Override
	public void createBindings(ReactiveBinder binder) {
		binder.bind("percent",this::renderProgress);
	}


	@Override
	public void registerListeners(CliController controller) {}
}
class CliController {

}