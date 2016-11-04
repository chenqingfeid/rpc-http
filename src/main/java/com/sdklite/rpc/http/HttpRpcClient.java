package com.sdklite.rpc.http;

import com.sdklite.rpc.RpcClient;

public interface HttpRpcClient extends RpcClient<HttpRpcRequest, HttpRpcResponse> {

    @Override
    HttpRpcClient.Builder newBuilder();

    @Override
    HttpRpcRequest.Builder newRequestBuilder();

    @Override
    HttpRpcResponse.Builder newResponseBuilder();

    @Override
    HttpRpc newRpc(HttpRpcRequest request);

    public interface Builder extends RpcClient.Builder<HttpRpcRequest, HttpRpcResponse> {

        @Override
        HttpRpcClient build();

    }

}
