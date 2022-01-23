package com.niton.reactj.objects.exceptions;

import com.niton.reactj.api.exceptions.ReactiveException;

public class SubjectCallException extends ReactiveException {
    public SubjectCallException() {
        super("ProxySubjects can't be called directly (without proxy)");
    }
}
