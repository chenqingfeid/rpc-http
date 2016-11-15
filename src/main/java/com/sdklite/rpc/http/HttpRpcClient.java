package com.sdklite.rpc.http;

import com.sdklite.rpc.RpcClient;

/**
 * Represents the client of HTTP RPC
 * 
 * @author johnsonlee
 *
 */
public interface HttpRpcClient extends RpcClient<HttpRpcRequest, HttpRpcResponse> {

    @Override
    HttpRpcClient.Builder newBuilder();

    @Override
    HttpRpcRequest.Builder newRequestBuilder();

    @Override
    HttpRpcResponse.Builder newResponseBuilder();

    @Override
    HttpRpc newRpc(final HttpRpcRequest request);

    /**
     * Represents the builder of {@link HttpRpcClient}
     * 
     * @author johnsonlee
     *
     */
    public interface Builder extends RpcClient.Builder<HttpRpcRequest, HttpRpcResponse> {

        @Override
        HttpRpcClient build();

    }

}
