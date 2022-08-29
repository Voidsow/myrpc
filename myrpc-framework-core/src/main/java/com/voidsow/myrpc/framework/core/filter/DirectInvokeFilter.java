package com.voidsow.myrpc.framework.core.filter;

public class DirectInvokeFilter extends AttachmentEqualityFilter {
    public DirectInvokeFilter() {
        super("url", wrapper -> wrapper.getHost() + ":" + wrapper.getPort());
    }
}
