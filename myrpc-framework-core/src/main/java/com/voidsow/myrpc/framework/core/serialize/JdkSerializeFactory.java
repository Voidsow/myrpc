package com.voidsow.myrpc.framework.core.serialize;

import java.io.*;

public class JdkSerializeFactory implements SerializeFactory {

    static final private String ERROR_MSG = "failed to serialize";

    @Override
    public <T> byte[] serialize(T t) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            //序列化
            ObjectOutputStream output = new ObjectOutputStream(os);
            output.writeObject(t);
            output.flush();
            output.close();
            return os.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(ERROR_MSG, e);
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        try {
            ByteArrayInputStream is = new ByteArrayInputStream(data);
            ObjectInputStream input = new ObjectInputStream(is);
            return (T) input.readObject();
        } catch (Exception e) {
            throw new RuntimeException(ERROR_MSG, e);
        }
    }
}
