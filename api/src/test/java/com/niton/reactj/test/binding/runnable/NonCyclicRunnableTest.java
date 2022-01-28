package com.niton.reactj.test.binding.runnable;

import com.niton.reactj.api.binding.runnable.NonCyclicRunnable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class NonCyclicRunnableTest {

    NonCyclicRunnable nonCyclicRunnable = null;

    @Test
    void run() {
        nonCyclicRunnable = new NonCyclicRunnable(this::deadFunction);
        assertDoesNotThrow(nonCyclicRunnable::run);
    }

    void deadFunction() {
        System.out.println("This is a dead function");
        nonCyclicRunnable.run();
    }
}