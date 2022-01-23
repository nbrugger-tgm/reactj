package com.niton.reactj.test.binding;

import com.niton.reactj.api.binding.ConsumerGroup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class ConsumerGroupTest {
    private final List<Object> consumed = new LinkedList<>();

    @Test
    void flow() {
        Consumer<Object> consumer = consumed::add;
        var              group    = new ConsumerGroup<>();
        Assertions.assertDoesNotThrow(
                () -> group.accept(null),
                "empty group should not throw exceptions"
        );
        group.add(consumer);
        group.accept(null);
        assertEquals(
                1, consumed.size(),
                "The added consumer should be called"
        );
        assertNull(
                consumed.get(0),
                "The added consumer should be called with the given value"
        );
        group.add(consumer);
        group.accept("test");
        assertEquals(
                3, consumed.size(),
                "All added consumer should be called"
        );
        assertArrayEquals(
                new Object[]{null, "test", "test"},
                consumed.toArray()
        );

    }
}