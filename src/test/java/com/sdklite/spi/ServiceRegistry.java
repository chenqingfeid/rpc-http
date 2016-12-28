package com.sdklite.spi;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sdklite.rpc.RpcClientFactory;
import com.sdklite.rpc.http.HttpRpcClientFactory;

class ServiceRegistry {

    private static final Map<Class<?>, Set<Class<?>>> sServices = new HashMap<Class<?>, Set<Class<?>>>();

    static {
        register(RpcClientFactory.class, HttpRpcClientFactory.class);
    }

    public static synchronized void register(final Class<?> serviceClass, final Class<?> providerClass) {
        Set<Class<?>> providers = sServices.get(serviceClass);
        if (null == providers) {
            providers = new HashSet<Class<?>>();
        }
        providers.add(providerClass);
        sServices.put(serviceClass, providers);
    }

    public static Set<Class<?>> get(final Class<?> clazz) {
        final Set<Class<?>> providers = sServices.get(clazz);
        return null == providers ? Collections.<Class<?>>emptySet() : Collections.unmodifiableSet(providers);
    }

}
