package com.myself.everything.core.interceptor;

import com.myself.everything.core.model.Thing;

@FunctionalInterface
public interface ThingInterceptor {
    void apply(Thing thing);
}
