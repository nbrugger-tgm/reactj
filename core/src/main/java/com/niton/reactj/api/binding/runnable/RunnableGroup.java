package com.niton.reactj.api.binding.runnable;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class RunnableGroup implements Runnable {
	private final List<Runnable> runnables;

	public RunnableGroup() {
		runnables = new LinkedList<>();
	}

	public RunnableGroup(Runnable... runnables) {
		this.runnables = Arrays.asList(runnables);
	}
	public void add(Runnable r) {
		runnables.add(r);
	}

	@Override
	public void run() {
		runnables.forEach(Runnable::run);
	}
}
