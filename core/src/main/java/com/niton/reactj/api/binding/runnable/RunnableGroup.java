package com.niton.reactj.api.binding.runnable;

import java.util.LinkedList;
import java.util.List;

public class RunnableGroup implements Runnable {
	private final List<Runnable> runnables = new LinkedList<>();

	public void add(Runnable r) {
		runnables.add(r);
	}

	@Override
	public void run() {
		runnables.forEach(Runnable::run);
	}
}
