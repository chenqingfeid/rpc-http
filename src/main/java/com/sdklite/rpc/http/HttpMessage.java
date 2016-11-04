package com.sdklite.rpc.http;

import com.sdklite.rpc.RpcMessage;

public abstract class HttpMessage implements RpcMessage {

    private final String url;

    public HttpMessage(final Builder builder) {
        this.url = builder.url;
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    public static abstract class Builder implements RpcMessage.Builder {

        private String url;

        public Builder setUrl(final String url) {
            this.url = url;
            return this;
        }

        @Override
        public abstract HttpMessage build();

    }
}
