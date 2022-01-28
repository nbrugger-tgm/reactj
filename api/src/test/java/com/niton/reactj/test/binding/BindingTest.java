package com.niton.reactj.test.binding;

import com.niton.reactj.api.binding.BaseBinding;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BindingTest {

    @Test
    void flow() {
        List<String> list = List.of("a", "b", "c");
        List<String> list2 = new ArrayList<>();
        var          iter = list.iterator();

        var bind = new BaseBinding<>(list2::add, iter::next);
        bind.run();
        assertEquals(1, list2.size(), "One 'run' should execute the binding one");
        assertDoesNotThrow(
                bind::run,
                "Two 'run' should not throw an exception -> Binding is reusable"
        );
        bind.run();
        assertLinesMatch(
                list, list2,
                "The list should be the same since every run should execute the binding"
        );
    }


}