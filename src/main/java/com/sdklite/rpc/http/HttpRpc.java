package com.sdklite.rpc.http;

import com.sdklite.rpc.Rpc;

/**
 * Represents the HTTP RPC
 * 
 * @author johnsonlee
 *
 */
public interface HttpRpc extends Rpc<HttpRpcRequest, HttpRpcResponse> {

    /**
     * Represents the HTTP RPC callback
     * 
     * @author johnsonlee
     *
     */
    public interface Callback extends Rpc.Callback<HttpRpcRequest, HttpRpcResponse> {
    }

    /**
     * Appends this RPC to the tail of execution queue
     * 
     * @param callback
     *            The RPC callback
     * @return the tag of this RPC
     */
    public Object enquque(final HttpRpc.Callback callback);

}
