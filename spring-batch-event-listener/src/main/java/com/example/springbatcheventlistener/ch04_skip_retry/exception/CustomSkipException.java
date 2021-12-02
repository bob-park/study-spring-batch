package com.example.springbatcheventlistener.ch04_skip_retry.exception;

public class CustomSkipException extends RuntimeException {

    public CustomSkipException(String message) {
        super(message);
    }
}
