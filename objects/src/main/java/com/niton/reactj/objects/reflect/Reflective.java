package com.niton.reactj.objects.reflect;

import java.util.Map;

public interface Reflective {
    /**
     * Performs multiple {@link #set(String, Object)} operations. One for every Map Entry
     *
     * @param changed the Map containing all changes
     */
    default void set(Map<String, Object> changed) {
        for (Map.Entry<String, Object> change : changed.entrySet()) {
            set(change.getKey(), change.getValue());
        }
    }

    /**
     * Updates a field in this object with the respective name (sentivie to @Reactive)
     *
     * @param property the name of the property to set (@Reactive respected)
     * @param value    the value to change to
     *
     * @throws Exception if anything goes wrong
     */
    void set(String property, Object value);

    /**
     * Get a map of all properties and their values.<br>
     * {@code Map<NameOfField,ValueOfField>} The name should be influenced by {@code
     *
     * @return the state of the object
     *
     * @Reactive("name")}
     */
    Map<String, Object> getState();
}
