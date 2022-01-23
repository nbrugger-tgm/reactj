package com.niton.reactj.api.binding;

import java.util.Objects;
import java.util.function.Function;

public final class Converters {
    public static final Function<?, String>       toString    = Objects::toString;
    public static final Function<String, Integer> parseInt    = Integer::parseInt;
    public static final Function<String, Long>    parseLong   = Long::parseLong;
    public static final Function<String, Double>  parseDouble = Double::parseDouble;
    public static final Function<String, Float>   parseFloat  = Float::parseFloat;
    public static final Function<String, Boolean> parseBool   = Boolean::parseBoolean;

    private Converters() {}
}
