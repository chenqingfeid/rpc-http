package com.sdklite.rpc.http;

import com.sdklite.rpc.RpcRequest;

public class HttpRpcRequest extends HttpMessage implements RpcRequest {

    private HttpRpcRequest(final Builder builder) {
        super(builder);
    }

    @Override
    public Builder newBuilder() {
        return new Builder(this);
    }

    public static class Builder extends HttpMessage.Builder implements RpcRequest.Builder {

        public Builder() {
        }

        private Builder(final HttpRpcRequest httpRpcRequest) {
        }

        @Override
        public HttpRpcRequest build() {
            return new HttpRpcRequest(this);
        }

    }

}
