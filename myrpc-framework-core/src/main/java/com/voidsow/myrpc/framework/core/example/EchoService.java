package com.voidsow.myrpc.framework.core.example;

import java.util.List;

public interface EchoService {
    String send(String str);

    List<String> get();
}
