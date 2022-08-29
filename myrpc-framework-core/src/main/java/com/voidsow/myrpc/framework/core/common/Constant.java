package com.voidsow.myrpc.framework.core.common;

public class Constant {
    //RPC协议魔数
    public static short MAGIC_NUMBER = 1603;

    public static long TIME_OUT_MILLIS = 3 * 1000;

    public static String ROUTER_TYPE_RANDOM = "random";

    public static String ROUTER_TYPE_ROTATE = "rotate";

    public static final String SERIALIZE_TYPE_JDK = "jdk";

    public static final String SERIALIZE_TYPE_HESSIAN = "hessian";

    public static final String SERIALIZE_TYPE_KRYO = "kryo";

    public static final String SERIALIZE_TYPE_JSON = "json";
}
