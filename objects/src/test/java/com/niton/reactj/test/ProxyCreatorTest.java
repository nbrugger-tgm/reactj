package com.niton.reactj.test;

import com.niton.reactj.api.proxy.InfusionAccessProvider;
import com.niton.reactj.objects.proxy.ProxyCreator;
import com.niton.reactj.test.models.Base;
import com.niton.reactj.test.models.WithFinalPublic;
import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class ProxyCreatorTest {

    @Test
    void withinDependency() {
        ProxyCreator creator = ProxyCreator.withinDependency();
        var          proxy   = creator.create(new Base());
        assertEquals(ProxyCreator.class.getPackageName(), proxy.getClass().getPackageName());
        assertEquals(
                ProxyCreator.class.getModule().getName(),
                proxy.getClass().getModule().getName()
        );
    }

    @Test
    void besideOrigin() {
        ProxyCreator creator = ProxyCreator.besideOrigin();
        var          proxy   = creator.create(new Base()).getObject();
        assertEquals(Base.class.getPackageName(), proxy.getClass().getPackageName());
        assertEquals(Base.class.getModule().getName(), proxy.getClass().getModule().getName());
    }

    @Test
    void custom() {
        String customPackage = "com.niton.reactj.test.custom.proxies";
        var custom = new InfusionAccessProvider() {

            @Override
            public MethodHandles.Lookup getLookup(Class<?> originClass) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String getPackage(Class<?> originClass) {
                return customPackage;
            }
        };
        ProxyCreator creator = ProxyCreator.custom(custom);
        var          base    = new Base();
        assertThrows(UnsupportedOperationException.class, () -> creator.create(base));
    }

    @Test
    void copyFinals() {
        var          from    = new WithFinalPublic("from");
        var          to      = new WithFinalPublic("to");
        ProxyCreator creator = ProxyCreator.besideOrigin();
        var          work    = System.getSecurityManager() == null;
        if (work) {
            AtomicBoolean error = new AtomicBoolean(false);
            assertDoesNotThrow(() -> error.set(creator.copyFinalFields(to, from)));
            if (!error.get())
                assertEquals("from", to.name);
            else
                assertEquals("to", to.name);
        } else {
            assertFalse(creator.copyFinalFields(to, from));
        }
    }
}