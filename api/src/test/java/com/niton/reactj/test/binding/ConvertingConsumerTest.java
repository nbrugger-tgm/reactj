package com.niton.reactj.test.binding;

import com.niton.reactj.api.binding.Converters;
import com.niton.reactj.api.binding.consumer.ConvertingConsumer;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConvertingConsumerTest {
    private int output;

    @Test
    void performConversion() {
        Consumer<Integer> doubler = i -> output = i * 2;

        var consumer = new ConvertingConsumer<>(doubler, Converters.parseInt);
        consumer.accept("2");
        assertEquals(4, output);
    }
}