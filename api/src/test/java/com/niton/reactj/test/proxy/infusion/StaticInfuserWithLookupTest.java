package com.niton.reactj.test.proxy.infusion;

import com.niton.reactj.api.proxy.infusion.StaticInfuser;
import com.niton.reactj.api.proxy.infusion.StaticInfuserWithLookup;
import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandles;

import static org.junit.jupiter.api.Assertions.*;

class StaticInfuserWithLookupTest {
	@Test
	void test() {
		var lookup = MethodHandles.lookup();
		var infuser = new StaticInfuserWithLookup(
				StaticInfuserWithLookupTest.class,
				lookup
		);
		assertEquals(
				"com.niton.reactj.test.proxy.infusion",
				infuser.getPackage(MethodHandles.class),
				"The package with static lookup should be static and independent of the parameter"
		);
		assertEquals(
				lookup, infuser.getLookup(MethodHandles.class),
				"The lookup should be the same as given in the ctor"
		);
	}

	@Test
	void without() {
		var lookup = MethodHandles.lookup();
		var infuser = new StaticInfuser(
				StaticInfuserWithLookupTest.class,
				lookup
		);
		assertEquals(
				"com.niton.reactj.test.proxy.infusion",
				infuser.getPackage(MethodHandles.class),
				"The package with static lookup should be static and independent of the parameter"
		);
		assertDoesNotThrow(() -> infuser.getLookup(AnchorClass.class)
		                                .accessClass(AnchorClass.class));
	}
}