package com.niton.reactj.test.binding;

import com.niton.reactj.api.binding.ConstantSupplier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConstantSupplierTest {
    @Test
    void flow() {
        var supplier = new ConstantSupplier<>("Hello World");
        assertEquals("Hello World", supplier.get());
    }
}