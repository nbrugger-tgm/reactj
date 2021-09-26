package com.niton.reactj.core.observer;

import java.util.Objects;

public class PropertyObservation {
	public final String propertyName;
	public final Object propertyValue;

	public PropertyObservation(String propertyName, Object propertyValue) {
		this.propertyName  = propertyName;
		this.propertyValue = propertyValue;
	}

	@Override
	public int hashCode() {
		return Objects.hash(propertyName, propertyValue);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PropertyObservation)) return false;
		PropertyObservation that = (PropertyObservation) o;
		return Objects.equals(propertyName, that.propertyName) &&
				Objects.equals(propertyValue, that.propertyValue);
	}
}
