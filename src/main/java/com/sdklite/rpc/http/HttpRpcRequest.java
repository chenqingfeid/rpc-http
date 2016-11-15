package com.sdklite.rpc.http;

import java.lang.reflect.Method;

import com.sdklite.net.http.HttpEntity;
import com.sdklite.net.http.HttpHeader;
import com.sdklite.net.http.HttpMessage;
import com.sdklite.net.http.HttpMethod;
import com.sdklite.rpc.RpcRequest;
import com.sdklite.rpc.RpcService;

/**
 * Represents the HTTP RPC request
 * 
 * @author johnsonlee
 *
 */
public class HttpRpcRequest extends HttpMessage implements RpcRequest {

    private final Object tag;
    private final String url;
    private final HttpMethod method;

    private HttpRpcRequest(final Builder builder) {
        super(builder);
        this.url = builder.url;
        this.tag = null != builder.tag ? builder.tag : this;
        this.method = builder.method;
    }

    @Override
    public Object getTag() {
        return this.tag;
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    /**
     * Returns the HTTP method
     */
    public HttpMethod getMethod() {
        return this.method;
    }

    /**
     * Determine if this request is secure
     */
    public boolean isSecure() {
        final int colon = this.url.indexOf(':');
        if (colon > 0) {
            return "https".equalsIgnoreCase(this.url.substring(0, colon));
        }

        return false;
    }

    @Override
    public Builder newBuilder() {
        return new Builder(this);
    }

    public static class Builder extends HttpMessage.Builder implements RpcRequest.Builder {

        private Object tag;

        private String url;

        private HttpMethod method;

        /**
         * Default constructor
         */
        public Builder() {
        }

        private Builder(final HttpRpcRequest httpRpcRequest) {
        }

        /**
         * Sets the tag
         * 
         * @param tag
         *            A tag
         * @return this builder
         */
        public Builder setTag(final Object tag) {
            this.tag = tag;
            return this;
        }

        @Override
        public Builder setUrl(final String url) {
            this.url = url;
            return this;
        }

        /**
         * Sets the HTTP method and message entity
         * 
         * @param method
         *            The HTTP method
         * @param entity
         *            The HTTP entity
         * @return this builder
         */
        public Builder setMethod(final HttpMethod method, final HttpEntity entity) {
            this.method = method;
            super.setEntity(entity);
            return this;
        }

        @Override
        public Builder setService(final Class<? extends RpcService> service, final Method method, final Object... args) {
            // TODO
            return this;
        }

        @Override
        public Builder addHeaders(final HttpHeader... headers) {
            super.addHeaders(headers);
            return this;
        }

        @Override
        public Builder addHeaders(final Iterable<HttpHeader> headers) {
            super.addHeaders(headers);
            return this;
        }

        @Override
        public Builder addHeader(final String name, final String value) {
            super.addHeader(name, value);
            return this;
        }

        @Override
        public Builder setEntity(final HttpEntity entity) {
            super.setEntity(entity);
            return this;
        }

        @Override
        public HttpRpcRequest build() {
            return new HttpRpcRequest(this);
        }

    }

}
