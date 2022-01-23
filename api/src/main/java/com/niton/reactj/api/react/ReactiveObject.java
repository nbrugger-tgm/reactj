package com.niton.reactj.api.react;


/**
 * The base class to make an Object reactive (usable in ReactiveComponents).
 * <p>
 * The most common way to use this component is by extending it and call {@link
 * ReactiveObject#react()} whenever needed
 */
public class ReactiveObject implements ReactiveForwarder {
    /**
     * A wrapper that warps this very object and is used as the {@link Reactable} implementation
     */
    private final ReactiveWrapper<ReactiveObject> thisWrapper = new ReactiveWrapper<>(this);

    @Override
    public Reactable getReactableTarget() {
        return thisWrapper;
    }

}
