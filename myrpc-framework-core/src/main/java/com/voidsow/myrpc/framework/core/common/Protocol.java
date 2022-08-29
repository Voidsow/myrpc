package com.voidsow.myrpc.framework.core.common;

import static com.voidsow.myrpc.framework.core.common.Constant.MAGIC_NUMBER;

public class Protocol {
    static final long serialVersionUID = 3439482309482390L;
    short magicNumber = MAGIC_NUMBER;
    int length;
    byte[] content;

    public Protocol(byte[] content) {
        this.length = content.length;
        this.content = content;
    }

    public short getMagicNumber() {
        return magicNumber;
    }

    public void setMagicNumber(short magicNumber) {
        this.magicNumber = magicNumber;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
