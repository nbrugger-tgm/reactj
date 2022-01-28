package com.niton.reactj.test.binding;

import com.niton.reactj.api.binding.BaseBinding;
import com.niton.reactj.api.binding.Binding;
import com.niton.reactj.api.binding.NonCyclicBinding;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NonCyclicBindingTest {
    Runnable runnable;
    @Test
    void run(){
        Binding<String> b = new NonCyclicBinding<>(new BaseBinding<>(this::consume,()->"Hello World"));
        runnable = b;
        assertDoesNotThrow(b::run);
    }

    private <T> void consume(T t) {
        System.out.println(t);
        runnable.run();
    }
}