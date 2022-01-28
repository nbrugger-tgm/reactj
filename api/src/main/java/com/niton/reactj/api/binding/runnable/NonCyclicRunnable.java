package com.niton.reactj.api.binding.runnable;

/**
 * A runnable whose run method is not cyclic. Meaning it is impossible to cause a stack
 * overflow/recursion.
 */
public class NonCyclicRunnable implements Runnable {
    /**
     * The actual runnable to execute
     */
    private final Runnable subRunnable;
    private       boolean  executing = false;

    public NonCyclicRunnable(Runnable subRunnable) {
        this.subRunnable = subRunnable;
    }

    @Override
    public void run() {
        if (executing)
            return;
        executing = true;
        subRunnable.run();
        executing = false;
    }
}
