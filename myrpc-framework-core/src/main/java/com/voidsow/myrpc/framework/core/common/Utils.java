package com.voidsow.myrpc.framework.core.common;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {
    static private ObjectMapper mapper = new ObjectMapper();

    static public ObjectMapper getMapper() {
        return mapper;
    }
}
