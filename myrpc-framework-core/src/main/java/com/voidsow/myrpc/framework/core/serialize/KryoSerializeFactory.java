package com.voidsow.myrpc.framework.core.serialize;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class KryoSerializeFactory implements SerializeFactory {
    static private final ThreadLocal<Kryo> kryos = new ThreadLocal<>() {
        @Override
        protected Kryo initialValue() {
            return new Kryo();
        }
    };

    @Override
    public <T> byte[] serialize(T t) {
        Kryo kryo = kryos.get();
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        Output output = new Output(byteBuffer);
        kryo.writeClassAndObject(output, t);
        output.close();
        return byteBuffer.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        Kryo kryo = kryos.get();
        ByteArrayInputStream buffer = new ByteArrayInputStream(data);
        Input input = new Input(buffer);
        return (T) kryo.readClassAndObject(input);
    }
}
