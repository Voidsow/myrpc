package com.voidsow.myrpc.framework.core.serialize;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voidsow.myrpc.framework.core.common.Utils;

import java.io.IOException;

public class JsonSerializeFactory implements SerializeFactory {
    static final ObjectMapper mapper = Utils.getMapper();

    @Override
    public <T> byte[] serialize(T t) {
        try {
            return mapper.writeValueAsBytes(t);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        try {
            return mapper.readValue(data, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
