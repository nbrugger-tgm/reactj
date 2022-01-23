package com.niton.reactj.test.models;


public class TestData {
    public int      id;
    public TestEnum c = TestEnum.RED;

    public TestEnum getC() {
        return c;
    }

    public void setC(TestEnum c) {
        this.c = c;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setColor(TestEnum c) {
        this.c = c;
    }

    public enum TestEnum {
        RED,
        BLUE,
        GREEN
    }
}
