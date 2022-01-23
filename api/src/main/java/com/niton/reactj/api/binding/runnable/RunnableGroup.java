package com.niton.reactj.api.binding.runnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * A group of runnables to be executed all at once (no multi-threading)
 */
public class RunnableGroup implements Runnable {
	private final List<Runnable> runnables;

	public RunnableGroup() {
		runnables = new LinkedList<>();
	}

	public RunnableGroup(Runnable... runnables) {
		this.runnables = new ArrayList<>(Arrays.asList(runnables));
	}

	/**
	 * Adds a runnable to be executed on {@link #run()}
	 */
	public void add(Runnable runnable) {
		runnables.add(runnable);
	}

	/**
	 * Runs all previously added runnables
	 */
	@Override
	public void run() {
		runnables.forEach(Runnable::run);
	}

	public Runnable[] toArray() {
		return runnables.toArray(Runnable[]::new);
	}

	public void remove(Runnable runnable) {
		runnables.remove(runnable);
	}

	public int size() {
		return runnables.size();
	}
}
