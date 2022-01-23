package com.niton.reactj.api.binding.runnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * A group of runnables to be executed all at once (sequential not async)
 */
public class RunnableGroup implements Runnable {
    /**
     * The runnables to execute on {@link #run()}
     */
    private final List<Runnable> runnables;

    public RunnableGroup() {
        runnables = new LinkedList<>();
    }

    /**
     * @param runnables the runnables to initially add to this group
     */
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

    /**
     * @return the runnables this group will execute as array
     */
    public Runnable[] toArray() {
        return runnables.toArray(Runnable[]::new);
    }

    /**
     * @param runnable the runnable to remove
     */
    public void remove(Runnable runnable) {
        runnables.remove(runnable);
    }

    /**
     * @return the amount of runnables in this group
     */
    public int size() {
        return runnables.size();
    }
}
