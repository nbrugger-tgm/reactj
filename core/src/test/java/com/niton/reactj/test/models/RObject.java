package com.niton.reactj.test.models;

import com.niton.reactj.api.react.ReactiveObject;

import java.io.Serializable;
import java.util.Objects;

public class RObject {
	public static class WithoutHashEquals extends ReactiveObject implements Serializable {
		public int i = 0;

		public WithoutHashEquals() {
		}
	}

	public static class WithHashEquals extends ReactiveObject implements Serializable {
		public int i = 0;

		public WithHashEquals() {
		}

		@Override
		public int hashCode() {
			return Objects.hash(i);
		}

		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof WithHashEquals)) return false;
			WithHashEquals yeet = (WithHashEquals) o;
			return i == yeet.i;
		}
	}
}
