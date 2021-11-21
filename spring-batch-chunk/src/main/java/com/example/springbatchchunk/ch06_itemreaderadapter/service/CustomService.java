package com.example.springbatchchunk.ch06_itemreaderadapter.service;

public class CustomService {

    private int cnt = 0;

    public String customRead() {

        if (cnt > 10) {
            return null;
        }

        return "item" + cnt++;
    }

}
