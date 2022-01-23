package com.niton.reactj.api.exceptions;

import java.util.function.Supplier;

import static java.lang.String.format;

public final class Exceptions {
    private Exceptions() {}

    public static Supplier<RuntimeException> noImplementation(Class<?> cls) {
        return () -> new RuntimeException(new ClassNotFoundException(format(
                "No implementation found for class: %s." +
                        " Maybe you forgot to add a 'reactj api' implementation," +
                        " if you don't want to use the 'core' implementation, please implement" +
                        " the class yourself and provide it via JPMS/SPI",
                cls.getName()
        )));
    }
}
