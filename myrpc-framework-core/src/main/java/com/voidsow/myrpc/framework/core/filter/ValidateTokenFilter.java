package com.voidsow.myrpc.framework.core.filter;

import com.voidsow.myrpc.framework.core.common.Invocation;

public class ValidateTokenFilter implements ServerFilter {
    @Override
    public boolean filter(Invocation invocation) {
        return true;
    }
}
