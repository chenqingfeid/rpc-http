package com.sdklite.rpc.http;

import com.sdklite.net.AbstractUriHandler;
import com.sdklite.rpc.RpcClientFactory;
import com.sdklite.rpc.RpcContext;
import com.sdklite.spi.annotation.ServiceProvider;

/**
 * The HTTP RPC implementation of {@link RpcClientFactory}
 * 
 * @author johnsonlee
 *
 */
@ServiceProvider(RpcClientFactory.class)
public class HttpRpcClientFactory extends AbstractUriHandler implements RpcClientFactory<Object> {

    public HttpRpcClientFactory() {
        super("http", "https");
    }

    @Override
    public HttpRpcClient newRpcClient(final RpcContext<Object> context) {
        return new OkHttpRpcClient.Builder(context).build();
    }

}
