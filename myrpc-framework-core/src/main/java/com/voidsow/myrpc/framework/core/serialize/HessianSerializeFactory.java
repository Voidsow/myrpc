package com.voidsow.myrpc.framework.core.serialize;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HessianSerializeFactory implements SerializeFactory {
    @Override
    public <T> byte[] serialize(T t) {
        try {
            ByteArrayOutputStream byteOs = new ByteArrayOutputStream();
            Hessian2Output output = new Hessian2Output(byteOs);
            output.writeObject(t);
            output.flush();
            output.completeMessage();
            output.close();
            return byteOs.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        try {
            ByteArrayInputStream byteInput = new ByteArrayInputStream(data);
            Hessian2Input hessianParser = new Hessian2Input(byteInput);
            return (T) hessianParser.readObject(clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
