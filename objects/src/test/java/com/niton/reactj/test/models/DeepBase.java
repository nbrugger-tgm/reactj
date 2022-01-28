package com.niton.reactj.test.models;

import com.niton.reactj.objects.annotations.ReactiveResolution;

import static com.niton.reactj.objects.annotations.ReactiveResolution.ReactiveResolutionType.DEEP;

@ReactiveResolution(DEEP)
public class DeepBase extends Base {
    private int c;

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }
}
