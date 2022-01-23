package com.niton.reactj.test.util;

import com.niton.reactj.api.util.ReflectiveUtil;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class ReflectiveUtilTest {
    private static Method executeCallMethod;
    private static String executeCallMethodSig;
    private static Method returnPassMethod;

    static {
        try {
            executeCallMethod    = com.niton.reactj.test.util.ReflectiveUtilTest.class.getDeclaredMethod(
                    "executeCall");
            executeCallMethodSig = ReflectiveUtil.getMethodSignature(executeCallMethod);
            returnPassMethod     = TestClass.class.getDeclaredMethod("returnPass", String.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static class TestClass {
        public String returnPass(String val) {
            return val;
        }
    }

    public static class ExtendingTextClass extends TestClass {
        @Override
        public String returnPass(String val) {
            return val + val;
        }
    }

    public static class FieldClass {
        private final static int staticFinal  = 0;
        private static       int staticMutable;
        public final         int publicFinal  = 0;
        private final        int privateFinal = 0;
        public               int publicMutable;
        private              int privateMutable;
    }

    @Test
    void invalidMethodParameterException() {
        Exception exc = ReflectiveUtil.invalidMethodParameterException(executeCallMethod, 67);
        assertTrue(
                exc.getMessage().contains(executeCallMethodSig),
                "Error message should contain signature"
        );
        assertTrue(
                exc.getMessage().contains("Integer"),
                "Error message should contain actual type (" + exc.getMessage() + ")"
        );
    }

    @Test
    void getMethodSignature() throws NoSuchMethodException {
        assertEquals("ReflectiveUtilTest.executeCall()", executeCallMethodSig);
        assertEquals(
                "Integer.parseInt(java.lang.String)",
                ReflectiveUtil.getMethodSignature(Integer.class.getMethod(
                        "parseInt",
                        String.class
                ))
        );
        assertEquals(
                "Integer.compare(int, int)",
                ReflectiveUtil.getMethodSignature(Integer.class.getMethod(
                        "compare",
                        int.class,
                        int.class
                ))
        );
    }

    @Test
    void executeCall() throws InvocationTargetException, IllegalAccessException {
        String arg = "SOME_PASS_VAL";
        String res = (String) ReflectiveUtil.executeCall(new TestClass(), returnPassMethod, arg);
        assertEquals(arg, res);

        assertThrows(
                IllegalArgumentException.class,
                () -> ReflectiveUtil.executeCall(Object.class, returnPassMethod, "SOME_FAIL_VAL")
        );
    }

    @Test
    void getOriginMethod() throws NoSuchMethodException {
        Method extended   = ExtendingTextClass.class.getDeclaredMethod("returnPass", String.class);
        Method overridden = ReflectiveUtil.getOriginMethod(extended, TestClass.class);
        assertEquals(returnPassMethod, overridden);
    }

    @Test
    void isFitting() {
        Integer boxed = 12;
        assertTrue(ReflectiveUtil.isFitting(boxed, int.class));
        assertTrue(ReflectiveUtil.isFitting(12, Integer.class));
        assertTrue(ReflectiveUtil.isFitting(new Object(), Object.class));
        assertTrue(ReflectiveUtil.isFitting("String", Object.class));
        assertFalse(ReflectiveUtil.isFitting(new Object(), String.class));
    }

    @Test
    void isMutableInstanceVar() throws NoSuchFieldException {
        assertTrue(ReflectiveUtil.isMutableInstanceVar(FieldClass.class.getDeclaredField(
                "privateMutable")));
        assertTrue(ReflectiveUtil.isMutableInstanceVar(FieldClass.class.getDeclaredField(
                "publicMutable")));
        assertFalse(ReflectiveUtil.isMutableInstanceVar(FieldClass.class.getDeclaredField(
                "privateFinal")));
        assertFalse(ReflectiveUtil.isMutableInstanceVar(FieldClass.class.getDeclaredField(
                "publicFinal")));
        assertFalse(ReflectiveUtil.isMutableInstanceVar(FieldClass.class.getDeclaredField(
                "staticMutable")));
        assertFalse(ReflectiveUtil.isMutableInstanceVar(FieldClass.class.getDeclaredField(
                "staticFinal")));
    }

    @Test
    void isStatic() throws NoSuchFieldException {
        assertTrue(ReflectiveUtil.isStatic(FieldClass.class.getDeclaredField("staticFinal")));
        assertTrue(ReflectiveUtil.isStatic(FieldClass.class.getDeclaredField("staticMutable")));
        assertFalse(ReflectiveUtil.isStatic(FieldClass.class.getDeclaredField("privateFinal")));
        assertFalse(ReflectiveUtil.isStatic(FieldClass.class.getDeclaredField("publicMutable")));
    }
}