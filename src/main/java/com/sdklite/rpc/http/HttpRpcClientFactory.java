package com.sdklite.rpc.http;

import com.sdklite.net.AbstractUriHandler;
import com.sdklite.rpc.RpcClientFactory;
import com.sdklite.spi.annotation.ServiceProvider;

@ServiceProvider(RpcClientFactory.class)
public class HttpRpcClientFactory extends AbstractUriHandler implements RpcClientFactory {

    public HttpRpcClientFactory() {
        super("http", "https");
    }

    @Override
    public HttpRpcClient newRpcClient() {
        return new OkHttpRpcClient.Builder().build();
    }

}
