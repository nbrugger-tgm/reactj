package com.niton.reactj.api.binding;

import java.util.Objects;
import java.util.function.Function;

/**
 * A set of common converters. Mostly for String/Objects/Natives.
 */
public final class Converters {
    @SuppressWarnings("java:S1845")//since it rly represents the toString method this should be fine
    public static final Function<?, String>       toString    = Objects::toString;
    public static final Function<String, Integer> parseInt    = Integer::parseInt;
    public static final Function<String, Long>    parseLong   = Long::parseLong;
    public static final Function<String, Double>  parseDouble = Double::parseDouble;
    public static final Function<String, Float>   parseFloat  = Float::parseFloat;
    public static final Function<String, Boolean> parseBool   = Boolean::parseBoolean;

    private Converters() {}
}
