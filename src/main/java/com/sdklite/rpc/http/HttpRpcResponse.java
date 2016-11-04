package com.sdklite.rpc.http;

import com.sdklite.rpc.RpcResponse;

public class HttpRpcResponse extends HttpMessage implements RpcResponse {

    private HttpRpcResponse(final Builder builder) {
        super(builder);
    }

    @Override
    public <T> T getContent() {
        return null;
    }

    @Override
    public Builder newBuilder() {
        return new Builder(this);
    }

    public static class Builder extends HttpMessage.Builder implements RpcResponse.Builder {

        public Builder() {
        }

        private Builder(final HttpRpcResponse httpRpcResponse) {
        }

        @Override
        public HttpRpcResponse build() {
            return new HttpRpcResponse(this);
        }

    }

}
