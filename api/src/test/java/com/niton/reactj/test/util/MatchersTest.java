package com.niton.reactj.test.util;

import com.niton.reactj.api.util.Matchers;
import net.bytebuddy.description.method.MethodDescription.ForLoadedMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
		assertTrue(Matchers.from(Base.class).matches(new ForLoadedMethod(overridden)));
		assertTrue(matcher.matches(new ForLoadedMethod(overridden)));
		assertFalse(
				matcher.matches(new ForLoadedMethod(nonOverridden)),
				"boo does not overwrites Base.boo() therefore the matcher should fail"
		);
	}
}