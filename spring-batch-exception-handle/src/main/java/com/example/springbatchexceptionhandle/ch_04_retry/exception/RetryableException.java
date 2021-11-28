package com.example.springbatchexceptionhandle.ch_04_retry.exception;

public class RetryableException extends RuntimeException {

    public RetryableException() {
        super("Retry");
    }

    public RetryableException(String message) {
        super(message);
    }
}
