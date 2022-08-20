package com.voidsow.myrpc.framework.core.common.event;

import com.voidsow.myrpc.framework.core.common.event.data.UrlChange;

public class UpdateEvent implements Event {
    UrlChange urlChange;

    public UpdateEvent(UrlChange urlChange) {
        this.urlChange = urlChange;
    }

    public UrlChange getUrlChange() {
        return urlChange;
    }
}