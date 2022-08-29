package com.voidsow.myrpc.framework.core.filter;

import com.voidsow.myrpc.framework.core.common.event.data.ChannelFutureWrapper;

public class ClientGroupFilter extends AttachmentEqualityFilter {

    public ClientGroupFilter() {
        super("group", ChannelFutureWrapper::getGroup);
    }
}
