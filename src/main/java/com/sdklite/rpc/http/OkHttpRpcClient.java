package com.sdklite.rpc.http;

import java.net.CookieHandler;
import java.net.InetAddress;
import java.net.Proxy;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import com.sdklite.net.DnsResolver;
import com.sdklite.rpc.RpcContext;
import com.sdklite.rpc.RpcInterceptor;
import com.squareup.okhttp.Dispatcher;
import com.squareup.okhttp.Dns;
import com.squareup.okhttp.OkHttpClient;

class OkHttpRpcClient implements HttpRpcClient {

    final OkHttpClient delegate;

    private OkHttpRpcClient(final Builder builder) {
        this.delegate = builder.delegate;
    }

    @Override
    public void cancel(final Object tag) {
        this.delegate.cancel(tag);
    }

    @Override
    public Builder newBuilder() {
        return new Builder(this);
    }

    @Override
    public HttpRpcRequest.Builder newRequestBuilder() {
        return new HttpRpcRequest.Builder();
    }

    @Override
    public HttpRpcResponse.Builder newResponseBuilder() {
        return new HttpRpcResponse.Builder();
    }

    @Override
    public HttpRpc newRpc(final HttpRpcRequest request) {
        return new OkHttpRpc(this, request);
    }

    @Override
    public ExecutorService getExecutorService() {
        return this.delegate.getDispatcher().getExecutorService();
    }

    @Override
    public DnsResolver getDnsResolver() {
        final Dns dns = this.delegate.getDns();

        if (dns instanceof DnsWrapper) {
            return ((DnsWrapper) dns).resolver;
        }

        return new DnsResolver() {
            @Override
            public List<InetAddress> resolve(final String hostname) throws UnknownHostException {
                return dns.lookup(hostname);
            }
        };
    }

    @Override
    public long getConnectTimeout() {
        return this.delegate.getConnectTimeout();
    }

    @Override
    public long getReadTimeout() {
        return this.delegate.getReadTimeout();
    }

    @Override
    public long getWriteTimeout() {
        return this.delegate.getWriteTimeout();
    }

    @Override
    public SocketFactory getSocketFactory() {
        return this.delegate.getSocketFactory();
    }

    @Override
    public SSLSocketFactory getSSLSocketFactory() {
        return this.delegate.getSslSocketFactory();
    }

    @Override
    public HostnameVerifier getHostnameVerifier() {
        return this.delegate.getHostnameVerifier();
    }

    @Override
    public CookieHandler getCookieHandler() {
        return this.delegate.getCookieHandler();
    }

    @Override
    public Proxy getProxy() {
        return this.delegate.getProxy();
    }

    static final class Builder implements HttpRpcClient.Builder {

        private final OkHttpClient delegate;

        public Builder(final RpcContext<Object> contextProvider) {
            this.delegate = newDefaultOkHttpClient();
        }

        private Builder(final OkHttpRpcClient client) {
            this.delegate = client.delegate;
        }

        public Builder addInterceptor(final HttpRpcInterceptor interceptor) {
            return this.addInterceptor0(interceptor);
        }

        @Override
        public Builder addInterceptor(final RpcInterceptor<HttpRpcRequest, HttpRpcResponse> interceptor) {
            return this.addInterceptor0(interceptor);
        }

        private Builder addInterceptor0(final RpcInterceptor<HttpRpcRequest, HttpRpcResponse> interceptor) {
            this.delegate.interceptors().add(new OkHttpRpc.OkHttpRpcInterceptor(interceptor));
            return this;
        }

        @Override
        public Builder setExecutorService(final ExecutorService executor) {
            this.delegate.setDispatcher(new Dispatcher(executor));
            return this;
        }

        @Override
        public Builder setDnsResolver(final DnsResolver resolver) {
            this.delegate.setDns(new DnsWrapper(resolver));
            return this;
        }

        @Override
        public Builder setConnectTimeout(final long timeout) {
            this.delegate.setConnectTimeout(timeout, TimeUnit.MILLISECONDS);
            return this;
        }

        @Override
        public Builder setReadTimeout(final long timeout) {
            this.delegate.setReadTimeout(timeout, TimeUnit.MILLISECONDS);
            return this;
        }

        @Override
        public Builder setWriteTimeout(final long timeout) {
            this.delegate.setWriteTimeout(timeout, TimeUnit.MILLISECONDS);
            return this;
        }

        @Override
        public Builder setSocketFactory(final SocketFactory socketFactory) {
            this.delegate.setSocketFactory(socketFactory);
            return this;
        }

        @Override
        public Builder setSSLSocketFactory(final SSLSocketFactory sslSocketFactory) {
            this.delegate.setSslSocketFactory(sslSocketFactory);
            return this;
        }

        @Override
        public Builder setHostnameVerifier(final HostnameVerifier hostnameVerifier) {
            this.delegate.setHostnameVerifier(hostnameVerifier);
            return this;
        }

        @Override
        public Builder setCookieHandler(final CookieHandler cookieHandler) {
            this.delegate.setCookieHandler(cookieHandler);
            return this;
        }

        @Override
        public Builder setProxy(final Proxy proxy) {
            this.delegate.setProxy(proxy);
            return this;
        }

        @Override
        public OkHttpRpcClient build() {
            return new OkHttpRpcClient(this);
        }

        private static OkHttpClient newDefaultOkHttpClient() {
            final OkHttpClient client = new OkHttpClient();
            return client;
        }

    }

    private static final class DnsWrapper implements Dns {

        public final DnsResolver resolver;
        
        public DnsWrapper(final DnsResolver resolver) {
            this.resolver = resolver;
        }
        
        @Override
        public List<InetAddress> lookup(final String hostname) throws UnknownHostException {
            return resolver.resolve(hostname);
        }
        
    }
}
