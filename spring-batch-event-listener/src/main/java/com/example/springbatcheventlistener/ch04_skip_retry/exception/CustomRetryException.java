package com.example.springbatcheventlistener.ch04_skip_retry.exception;

public class CustomRetryException extends RuntimeException {

    public CustomRetryException(String message) {
        super(message);
    }
}
