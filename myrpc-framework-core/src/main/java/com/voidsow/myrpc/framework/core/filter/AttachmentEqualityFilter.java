package com.voidsow.myrpc.framework.core.filter;

import com.voidsow.myrpc.framework.core.common.Invocation;
import com.voidsow.myrpc.framework.core.common.event.data.ChannelFutureWrapper;

import java.util.List;

public class AttachmentEqualityFilter implements ClientFilter {
    interface Converter<T> {
        Object convert(T t);
    }

    Converter<ChannelFutureWrapper> converter;

    String attachment;

    public AttachmentEqualityFilter(String attachment, Converter<ChannelFutureWrapper> converter) {
        this.attachment = attachment;
        this.converter = converter;
    }

    @Override
    public void filter(List<ChannelFutureWrapper> providers, Invocation invocation) {
        Object value = invocation.getAttachments().get(attachment);
        providers.removeIf(wrapper -> !value.equals(converter.convert(wrapper)));
        if (providers.isEmpty())
            throw new RuntimeException(
                    String.format("no provider match %s for %s", attachment, value));
    }
}
