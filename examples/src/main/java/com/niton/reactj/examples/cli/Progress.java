package com.niton.reactj.examples.cli;

public class Progress {
    private double progress;

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public boolean isDone() {
        return progress >= 0.99;
    }
}
