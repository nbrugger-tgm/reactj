package com.niton.reactj.test.util;

import com.niton.reactj.core.observer.util.Matchers;
import net.bytebuddy.description.method.MethodDescription.ForLoadedMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Matchers")
class MatchersTest {
	private static class Base {
		public void foo() {

		}

		public void boo() {

		}
	}

	private static class Extender extends Base {
		@Override
		public void foo() {
		}

		public void bar() {

		}
	}

	@Test
	void overritesAnyOf() throws NoSuchMethodException {
		var matcher = Matchers.overwritesAnyOf(
				Base.class.getDeclaredMethod("foo"),
				Base.class.getDeclaredMethod("boo")
		);
		var overridden    = Extender.class.getDeclaredMethod("foo");
		var nonOverridden = Extender.class.getMethod("bar");
		assertTrue(matcher.matches(new ForLoadedMethod(overridden)));
		assertFalse(
				matcher.matches(new ForLoadedMethod(nonOverridden)),
				"boo does not overwrites Base.boo() therefore the matcher should fail"
		);
	}
}