package com.example.springbatchexceptionhandle.ch_03_skip.exception;

public class NoSkippableException extends Exception {

    public NoSkippableException(String s) {
        super(s);
    }
}
