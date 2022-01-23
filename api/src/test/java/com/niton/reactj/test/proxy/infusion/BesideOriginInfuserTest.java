package com.niton.reactj.test.proxy.infusion;

import com.niton.reactj.api.proxy.infusion.BesideOriginInfuser;
import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandles;

import static org.junit.jupiter.api.Assertions.*;

class BesideOriginInfuserTest {
    @Test
    void testAccess() {
        var acc = new BesideOriginInfuser(MethodHandles.lookup());
        assertEquals(
                "com.niton.reactj.test.proxy.infusion",
                acc.getPackage(BesideOriginInfuserTest.class),
                "The package should be the package of the param class"
        );
        assertEquals(
                "java.lang.invoke",
                acc.getPackage(MethodHandles.class),
                "The package should be the package of the param class"
        );
        assertDoesNotThrow(() -> acc.getLookup(BesideOriginInfuserTest.class)
                                    .accessClass(BesideOriginInfuserTest.class));
    }
}