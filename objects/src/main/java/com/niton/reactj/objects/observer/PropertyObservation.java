package com.niton.reactj.objects.observer;

import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.Objects;

public class PropertyObservation<M> {
    public final String propertyName;
    public final Object propertyValue;
    public final M      observed;

    public PropertyObservation(String propertyName, Object propertyValue, M observed) {
        if (propertyName == null || propertyValue == null || observed == null)
            throw new IllegalArgumentException("PropertyObservation can't have 'null' parameters");
        this.propertyName  = propertyName;
        this.propertyValue = propertyValue;
        this.observed      = observed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyName, propertyValue, observed);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;

        if (!(object instanceof PropertyObservation)) return false;

        PropertyObservation<?> that = (PropertyObservation<?>) object;

        return new EqualsBuilder()
                .append(propertyName, that.propertyName)
                .append(propertyValue, that.propertyValue)
                .append(observed, that.observed)
                .isEquals();
    }
}
