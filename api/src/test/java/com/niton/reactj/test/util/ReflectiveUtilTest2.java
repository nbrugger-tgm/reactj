package com.niton.reactj.test.util;

import com.niton.reactj.api.util.ReflectiveUtil;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.*;

class ReflectiveUtilTest2 {


    @Test
    void getMethodSignature() throws NoSuchMethodException {
        assertEquals(
                "String.toString()",
                ReflectiveUtil.getMethodSignature(String.class.getMethod("toString"))
        );
        assertEquals(
                "Integer.parseInt(java.lang.String)",
                ReflectiveUtil.getMethodSignature(
                        Integer.class.getMethod("parseInt", String.class)
                )
        );
    }

    @Test
    void getMethodParamSignature() throws NoSuchMethodException {
        assertEquals(
                "java.lang.Object, java.lang.Object",
                ReflectiveUtil.getMethodParamSignature(BiConsumer.class.getMethod(
                        "accept",
                        Object.class,
                        Object.class
                ))
        );
    }

    @Test
    void executeCall()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String s       = "   padded   ";
        var    trim    = String.class.getMethod("trim");
        var    trimmed = ReflectiveUtil.executeCall(s, trim);

        assertEquals("padded", trimmed);
    }

    @Test
    void getOriginMethod() throws NoSuchMethodException {
        var strEquals = String.class.getMethod("equals", Object.class);
        var objEquals = Object.class.getMethod("equals", Object.class);
        var found     = ReflectiveUtil.getOriginMethod(strEquals, Object.class);
        assertEquals(objEquals, found);
    }

    @Test
    void isFitting() {
        assertTrue(ReflectiveUtil.isFitting("String.class", String.class));
        assertTrue(ReflectiveUtil.isFitting("String.class", Object.class));
        assertTrue(ReflectiveUtil.isFitting(12, Integer.class));
        assertTrue(ReflectiveUtil.isFitting(Integer.valueOf("12"), int.class));
        assertTrue(ReflectiveUtil.isFitting(12, Object.class));

        assertFalse(ReflectiveUtil.isFitting(new Object(), String.class));
    }

    @Test
    void unboxTypes() {
        assertArrayEquals(
                new Class[]{int.class, float.class},
                ReflectiveUtil.unboxTypes(Integer.class, Float.class)
        );
    }

    @Test
    void isMutableInstanceVar() {
        var flds = TestObj.class.getDeclaredFields();
        var lst  = new ArrayList<String>();
        for (var fld : flds) {
            if (ReflectiveUtil.isMutableInstanceVar(fld))
                lst.add(fld.getName());
        }
        assertEquals(2, lst.size());
        assertTrue(lst.contains("pub") && lst.contains("pri"));
    }

    @Test
    void isStatic() throws NoSuchFieldException {
        var cls = TestObj.class;
        assertTrue(ReflectiveUtil.isStatic(cls.getDeclaredField("fspub")));
        assertTrue(ReflectiveUtil.isStatic(cls.getDeclaredField("spub")));
        assertTrue(ReflectiveUtil.isStatic(cls.getDeclaredField("spri")));
        assertTrue(ReflectiveUtil.isStatic(cls.getDeclaredField("fspri")));


        assertFalse(ReflectiveUtil.isStatic(cls.getDeclaredField("fpub")));
        assertFalse(ReflectiveUtil.isStatic(cls.getDeclaredField("pub")));
        assertFalse(ReflectiveUtil.isStatic(cls.getDeclaredField("pri")));
        assertFalse(ReflectiveUtil.isStatic(cls.getDeclaredField("fpri")));
    }

    @Test
    void isFinal() throws NoSuchFieldException {

        var cls = TestObj.class;
        assertTrue(ReflectiveUtil.isFinal(cls.getDeclaredField("fspub")));
        assertTrue(ReflectiveUtil.isFinal(cls.getDeclaredField("fpub")));
        assertTrue(ReflectiveUtil.isFinal(cls.getDeclaredField("fpri")));
        assertTrue(ReflectiveUtil.isFinal(cls.getDeclaredField("fspri")));


        assertFalse(ReflectiveUtil.isFinal(cls.getDeclaredField("spub")));
        assertFalse(ReflectiveUtil.isFinal(cls.getDeclaredField("pub")));
        assertFalse(ReflectiveUtil.isFinal(cls.getDeclaredField("pri")));
        assertFalse(ReflectiveUtil.isFinal(cls.getDeclaredField("spri")));
    }

    private static class TestObj {
        public static final  int fspub = 0;
        private static final int fspri = 0;
        public static        int spub;
        private static       int spri;
        public final         int fpub  = 0;
        private final        int fpri  = 0;
        public               int pub;
        private              int pri;
    }
}