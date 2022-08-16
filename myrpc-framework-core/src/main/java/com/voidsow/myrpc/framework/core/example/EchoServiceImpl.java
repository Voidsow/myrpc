package com.voidsow.myrpc.framework.core.example;

import java.util.List;

public class EchoServiceImpl implements EchoService {
    @Override
    public String send(String str) {
        return str;
    }

    @Override
    public List<String> get() {
        return null;
    }
}
