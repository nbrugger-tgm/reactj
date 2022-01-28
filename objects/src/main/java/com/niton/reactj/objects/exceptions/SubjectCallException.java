package com.niton.reactj.objects.exceptions;

public class SubjectCallException extends UnsupportedOperationException {
    public SubjectCallException() {
        super("ProxySubjects can't be called directly (without proxy)");
    }
}
