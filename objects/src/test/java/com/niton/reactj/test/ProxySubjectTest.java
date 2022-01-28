package com.niton.reactj.test;

import com.niton.reactj.objects.proxy.ProxySubject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProxySubjectTest {

    ProxySubject sub =  new ProxySubject() {};

    @Test
    void reactEvent() {
        assertThrows(UnsupportedOperationException.class, () -> sub.reactEvent());
    }

    @Test
    void set() {
        assertThrows(UnsupportedOperationException.class, () -> sub.set("",""));
    }

    @Test
    void getState() {
        assertThrows(UnsupportedOperationException.class, () -> sub.getState());
    }
}