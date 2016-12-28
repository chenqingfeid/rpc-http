package com.sdklite.rpc.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.sdklite.net.MimeType;
import com.sdklite.net.http.HttpBody;
import com.sdklite.net.http.HttpEntity;
import com.sdklite.net.http.HttpHeader;
import com.sdklite.net.http.HttpMethod;
import com.sdklite.net.http.SimpleHttpHeader;
import com.sdklite.rpc.Rpc;
import com.sdklite.rpc.RpcException;
import com.sdklite.rpc.RpcInterceptor;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;

class OkHttpRpc implements HttpRpc {

    private final OkHttpRpcClient client;

    private final HttpRpcRequest request;

    public OkHttpRpc(final OkHttpRpcClient client, final HttpRpcRequest request) {
        this.client = client;
        this.request = request;
    }

    @Override
    public void cancel() {
        this.client.cancel(this.request.getTag());
    }

    @Override
    public HttpRpcResponse execute() throws RpcException {
        final Request req = createRequest(this.request);
        try {
            return parseResponse(this.request, this.client.delegate.newCall(req).execute());
        } catch (final IOException e) {
            throw new RpcException(e);
        }
    }

    @Override
    public Object enqueue(final Rpc.Callback<HttpRpcRequest, HttpRpcResponse> callback) {
        return this.enqueue0(callback);
    }

    @Override
    public Object enquque(final HttpRpc.Callback callback) {
        return this.enqueue0(callback);
    }

    private synchronized Object enqueue0(final Rpc.Callback<HttpRpcRequest, HttpRpcResponse> callback) {
        this.client.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final HttpRpcResponse response = execute();
                    if (null != callback) {
                        callback.onSuccess(response);
                    }
                } catch (final RpcException e) {
                    if (null != callback) {
                        callback.onFailure(request, e);
                    }
                }
            }
        });

        return this.request.getTag();
    }



    static final class OkHttpRpcInterceptor implements Interceptor {

        final RpcInterceptor<HttpRpcRequest, HttpRpcResponse> interceptor;

        public OkHttpRpcInterceptor(final RpcInterceptor<HttpRpcRequest, HttpRpcResponse> interceptor) {
            this.interceptor = interceptor;
        }

        @Override
        public Response intercept(final Interceptor.Chain chain) throws IOException {
            final HttpRpcRequest request = createHttpRpcRequest(chain.request());
            return parseHttpRpcResponse(this.interceptor.intercept(new RpcInterceptor.RpcChain<HttpRpcRequest, HttpRpcResponse>() {
                @Override
                public HttpRpcResponse proceed(final HttpRpcRequest request) throws IOException {
                    return parseResponse(request, chain.proceed(createRequest(request)));
                }

                @Override
                public HttpRpcRequest getRequest() {
                    return request;
                }
            }));
        }

    }

    private static HttpRpcResponse parseResponse(final HttpRpcRequest request, final Response response) {
        return new HttpRpcResponse.Builder()
                .setProtocol(response.protocol().toString())
                .setStatusCode(response.code())
                .addHeaders(createHttpHeaders(response.headers()))
                .setEntity(createHttpRpcResponseEntity(response))
                .setRequest(request)
                .build();
    }

    private static HttpEntity createHttpRpcResponseEntity(final Response response) {
        final ResponseBody entity = response.body();
        if (null == entity) {
            return null;
        }

        return new HttpBody() {
            @Override
            public MimeType getContentType() {
                return MimeType.parse(entity.contentType().toString());
            }
            
            @Override
            public InputStream getContent() throws IOException {
                return entity.byteStream();
            }

            @Override
            public long getContentLength() throws IOException {
                return entity.contentLength();
            }
        };
    }

    private static List<HttpHeader> createHttpHeaders(final Headers headers) {
        final List<HttpHeader> list = new ArrayList<HttpHeader>();
        for (int i = 0, n = headers.size(); i < n; i++) {
            list.add(new SimpleHttpHeader(headers.name(i), headers.value(i)));
        }
        return list;
    }

    private static Request createRequest(final HttpRpcRequest request) {
        return new Request.Builder()
                .url(request.getUrl())
                .headers(createHeaders(request.getHeaders()))
                .method(request.getMethod().name(), createRequestBody(request))
                .build();
    }

    private static RequestBody createRequestBody(final HttpRpcRequest request) {
        final HttpEntity entity = request.getEntity();
        if (null == entity) {
            return null;
        }

        return new RequestBody() {
            @Override
            public long contentLength() throws IOException {
                return entity.getContentLength();
            }

            @Override
            public void writeTo(final BufferedSink sink) throws IOException {
                entity.writeTo(sink.outputStream());
            }

            @Override
            public MediaType contentType() {
                return MediaType.parse(entity.getContentType().toString());
            }
        };
    }

    private static Headers createHeaders(final List<HttpHeader> headers) {
        final Headers.Builder builder = new Headers.Builder();
        for (final HttpHeader header : headers) {
            builder.add(header.getName(), header.getValue());
        }
        return builder.build();
    }

    private static HttpRpcRequest createHttpRpcRequest(final Request request) {
        return new HttpRpcRequest.Builder()
                .setUrl(request.urlString())
                .addHeaders(createHttpHeaders(request.headers()))
                .setMethod(HttpMethod.valueOf(request.method()), createHttpRpcRequestBody(request))
                .build();
    }

    private static HttpEntity createHttpRpcRequestBody(final Request request) {
        final RequestBody body = request.body();
        if (null == body) {
            return null;
        }

        return new HttpBody() {
            @Override
            public MimeType getContentType() {
                return MimeType.parse(body.contentType().toString());
            }

            @Override
            public InputStream getContent() throws IOException {
                final Buffer buffer = new Buffer();
                body.writeTo(buffer);
                return buffer.inputStream();
            }
        };
    }

    private static Response parseHttpRpcResponse(final HttpRpcResponse response) throws IOException {
        final HttpEntity entity = response.getEntity();
        return new Response.Builder()
                .request(createRequest(response.getRequest()))
                .protocol(Protocol.get(response.getProtocol()))
                .code(response.getStatusCode())
                .message(response.getReasonPhrase())
                .headers(createHeaders(response.getHeaders()))
                .body(null == entity ? null : new ResponseBody() {
                    @Override
                    public BufferedSource source() throws IOException {
                        final Buffer buffer = new Buffer();
                        entity.writeTo(buffer.outputStream());
                        return buffer;
                    }
                    
                    @Override
                    public MediaType contentType() {
                        return MediaType.parse(entity.getContentType().toString());
                    }
                    
                    @Override
                    public long contentLength() throws IOException {
                        return entity.getContentLength();
                    }
                }).build();
    }
}
