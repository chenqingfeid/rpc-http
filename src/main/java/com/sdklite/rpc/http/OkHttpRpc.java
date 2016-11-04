package com.sdklite.rpc.http;

import com.sdklite.rpc.Rpc;
import com.sdklite.rpc.RpcException;

public class OkHttpRpc implements HttpRpc {

    public OkHttpRpc(final OkHttpRpcClient client, HttpRpcRequest request) {
    }

    @Override
    public HttpRpcResponse execute() throws RpcException {
        return null;
    }

    @Override
    public void enqueue(Rpc.Callback<?> callback) {
    }

}
