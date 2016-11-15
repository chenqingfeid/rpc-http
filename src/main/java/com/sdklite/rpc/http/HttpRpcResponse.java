package com.sdklite.rpc.http;

import com.sdklite.net.http.HttpEntity;
import com.sdklite.net.http.HttpHeader;
import com.sdklite.net.http.HttpMessage;
import com.sdklite.rpc.RpcResponse;

public class HttpRpcResponse extends HttpMessage implements RpcResponse {

    private final HttpRpcRequest request;
    private final String protocol;
    private final int statusCode;
    private final String reasonPhrase;

    private HttpRpcResponse(final Builder builder) {
        super(builder);
        this.request = builder.request;
        this.protocol = builder.protocol;
        this.statusCode = builder.statusCode;
        this.reasonPhrase = builder.reasonPhrase;
    }

    @Override
    public <T> T getContent() {
        return null;
    }

    @Override
    public String getUrl() {
        return this.request.getUrl();
    }

    /**
     * Returns the request
     */
    public HttpRpcRequest getRequest() {
        return this.request;
    }

    /**
     * Returns the protocol identifier
     */
    public String getProtocol() {
        return this.protocol;
    }

    /**
     * Returns the status code
     */
    public int getStatusCode() {
        return this.statusCode;
    }

    /**
     * Returns the reason phrase
     */
    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

    /**
     * Determine if this response is successful
     * 
     * @return true if successful, otherwise, false is returned
     */
    public boolean isSuccessful() {
        return this.statusCode >= 200 && this.statusCode < 300;
    }

    @Override
    public Builder newBuilder() {
        return new Builder(this);
    }

    /**
     * Represents the builder of {@link HttpRpcResponse}
     * 
     * @author johnsonlee
     *
     */
    public static class Builder extends HttpMessage.Builder implements RpcResponse.Builder {

        private HttpRpcRequest request;
        private String protocol;
        private int statusCode;
        private String reasonPhrase;

        /**
         * Default constructor
         */
        public Builder() {
        }

        private Builder(final HttpRpcResponse response) {
            this.request = response.request;
        }

        /**
         * Sets the protocol identifier
         * 
         * @param protocol
         *            The protocol identifier
         * @return this builder
         */
        public Builder setProtocol(final String protocol) {
            this.protocol = protocol;
            return this;
        }

        /**
         * Sets the status code
         * 
         * @param code
         *            The status code
         * @return this builder
         */
        public Builder setStatusCode(final int code) {
            this.statusCode = code;
            return this;
        }

        /**
         * Sets the reason phrase
         * 
         * @param reason
         *            The reason phrase
         * @return this builder
         */
        public Builder setReasonPhrase(final String reason) {
            this.reasonPhrase = reason;
            return this;
        }

        /**
         * Sets the request
         * 
         * @param request
         *            The request
         * @return this builder
         */
        public Builder setRequest(final HttpRpcRequest request) {
            this.request = request;
            return this;
        }

        @Override
        public Builder addHeader(final String name, final String value) {
            super.addHeader(name, value);
            return this;
        }

        @Override
        public Builder addHeaders(final Iterable<HttpHeader> headers) {
            super.addHeaders(headers);
            return this;
        }

        @Override
        public Builder addHeaders(final HttpHeader... headers) {
            super.addHeaders(headers);
            return this;
        }

        @Override
        public Builder setEntity(final HttpEntity entity) {
            super.setEntity(entity);
            return this;
        }

        @Override
        public HttpRpcResponse build() {
            return new HttpRpcResponse(this);
        }

    }

}
