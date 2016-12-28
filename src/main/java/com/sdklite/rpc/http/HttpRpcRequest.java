package com.sdklite.rpc.http;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sdklite.io.Deserializer;
import com.sdklite.io.NullDeserializer;
import com.sdklite.io.Serializer;
import com.sdklite.io.annotation.Deserialization;
import com.sdklite.io.annotation.Serialization;
import com.sdklite.net.MimeType;
import com.sdklite.net.http.HttpBody;
import com.sdklite.net.http.HttpEntity;
import com.sdklite.net.http.HttpHeader;
import com.sdklite.net.http.HttpHeaders;
import com.sdklite.net.http.HttpMessage;
import com.sdklite.net.http.HttpMethod;
import com.sdklite.rpc.RpcRequest;
import com.sdklite.rpc.RpcService;
import com.sdklite.rpc.annotation.BodyParameter;
import com.sdklite.rpc.annotation.Path;
import com.sdklite.rpc.annotation.PathParameter;
import com.sdklite.rpc.annotation.QueryParameter;
import com.sdklite.rpc.http.annotation.Delete;
import com.sdklite.rpc.http.annotation.Get;
import com.sdklite.rpc.http.annotation.Head;
import com.sdklite.rpc.http.annotation.Patch;
import com.sdklite.rpc.http.annotation.Post;
import com.sdklite.rpc.http.annotation.Put;
import com.sdklite.util.GenericType;
import com.sdklite.util.Introspector;
import com.sdklite.util.TypeResolver;
import com.squareup.okhttp.HttpUrl;

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

    private final Serializer<?> serializer;
    private final Deserializer<?> deserializer;

    private HttpRpcRequest(final Builder builder) {
        super(builder);
        this.url = builder.url;
        this.tag = null != builder.tag ? builder.tag : this;
        this.method = builder.method;
        this.serializer = builder.serializer;
        this.deserializer = builder.deserializer;
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

    Serializer<?> getSerializer() {
        return this.serializer;
    }

    Deserializer<?> getDeserializer() {
        return this.deserializer;
    }

    @Override
    public Builder newBuilder() {
        return new Builder(this);
    }

    public static class Builder extends HttpMessage.Builder implements RpcRequest.Builder {

        @SuppressWarnings("rawtypes")
        public Deserializer deserializer;

        @SuppressWarnings("rawtypes")
        public Serializer serializer;

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

        public Builder get(final String url) {
            this.setUrl(url);
            return this.setMethod(HttpMethod.GET, null);
        }

        public Builder head(final String url) {
            this.setUrl(url);
            return this.setMethod(HttpMethod.HEAD, null);
        }

        public Builder patch(final String url, final HttpEntity entity) {
            this.setUrl(url);
            return this.setMethod(HttpMethod.PATCH, entity);
        }

        public Builder post(final String url, final HttpEntity entity) {
            this.setUrl(url);
            return this.setMethod(HttpMethod.POST, entity);
        }

        public Builder put(final String url, final HttpEntity entity) {
            this.setUrl(url);
            return this.setMethod(HttpMethod.PUT, entity);
        }

        public Builder delete(final String url) {
            this.setUrl(url);
            return this.setMethod(HttpMethod.DELETE, null);
        }

        public Builder delete(final String url, final HttpEntity entity) {
            this.setUrl(url);
            return this.setMethod(HttpMethod.DELETE, entity);
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
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public Builder setService(final Class<? extends RpcService> service, final Method method, final Object... args) {
            final HttpUrl uri = HttpUrl.parse(this.url);
            final HttpUrl.Builder uriBuilder = new HttpUrl.Builder();
            uriBuilder.scheme(uri.scheme());
            uriBuilder.host(uri.host());
            uriBuilder.encodedPath(uri.encodedPath());
            uriBuilder.encodedFragment(uri.encodedFragment());

            // add path
            if (service.isAnnotationPresent(Path.class)) {
                final String path = service.getAnnotation(Path.class).value();
                if (null != path && path.trim().length() > 0) {
                    final String[] segments = path.split("/");
                    if (null != segments && segments.length > 0) {
                        for (final String segment : segments) {
                            if (null != segment && segment.trim().length() > 0) {
                                uriBuilder.addPathSegment(segment);
                            }
                        }
                    }
                }
            }

            if (method.isAnnotationPresent(Path.class)) {
                final String path = method.getAnnotation(Path.class).value();
                if (null != path && path.trim().length() > 0) {
                    final String[] segments = path.split("/");
                    if (null != segments && segments.length > 0) {
                        for (final String segment : segments) {
                            if (null != segments && segments.length > 0) {
                                uriBuilder.addPathSegment(segment);
                            }
                        }
                    }
                } 
            }

            final List<RpcRequest.Parameter> queryParams = new ArrayList<RpcRequest.Parameter>();
            final List<RpcRequest.Parameter> pathParams = new ArrayList<RpcRequest.Parameter>();
            final List<RpcRequest.Parameter> bodyParams = new ArrayList<RpcRequest.Parameter>();
            final RpcService.Callback<?> callback = null != args && args.length > 0 && args[args.length - 1] instanceof RpcService.Callback ? (RpcService.Callback<?>) args[args.length - 1] : null;

            // add parameters
            final Annotation[][] paramAnnotations = method.getParameterAnnotations();
            if (null != paramAnnotations && paramAnnotations.length > 0) {
                for (int i = 0, n = paramAnnotations.length - (null == callback ? 0 : 1); i < n; i++) {
                    final Object value = args[i];
                    if (null == value) {
                        continue;
                    }

                    final Annotation[] annotations = paramAnnotations[i];
                    if (null == annotations || annotations.length <= 0) {
                        continue;
                    }

                    for (final Annotation annotation : annotations) {
                        final Class<?> annotationType = annotation.annotationType();

                        if (QueryParameter.class.equals(annotationType)) {
                            final String name = ((QueryParameter) annotation).value();
                            queryParams.add(new SimpleParameter(name, value));
                        } else if (BodyParameter.class.equals(annotationType)) {
                            final String name = ((BodyParameter) annotation).value();
                            bodyParams.add(new SimpleParameter(name, value));
                        } else if (PathParameter.class.equals(annotationType)) {
                            final String name = ((PathParameter) annotation).value();
                            pathParams.add(new SimpleParameter(name, value));
                        }
                    }
                }
            }

            if (queryParams.size() > 0) {
                for (final RpcRequest.Parameter parameter : inflate(queryParams)) {
                    uriBuilder.addQueryParameter(parameter.getName(), String.valueOf(parameter.getValue()));
                }
            }

            if (pathParams.size() > 0) {
                for (final RpcRequest.Parameter parameter : inflate(pathParams)) {
                    final String name = parameter.getName();
                    final Object value = parameter.getValue();
                    final List<String> segments = uriBuilder.build().pathSegments();

                    uriBuilder.encodedPath("");

                    for (String segment : segments) {
                        final int lbrace = segment.indexOf('{');
                        final int rbrace = segment.indexOf('}');

                        if (lbrace >= 0 && rbrace >= 0 && rbrace > lbrace && segment.substring(lbrace + 1, rbrace).equals(name)) {
                            final StringBuilder resolved = new StringBuilder();

                            if (lbrace > 0) {
                                resolved.append(segment.substring(0, lbrace));
                            }

                            resolved.append(value);

                            if (rbrace < segment.length() - 1) {
                                resolved.append(segment.substring(rbrace + 1));
                            }

                            segment = resolved.toString();
                        }

                        uriBuilder.addPathSegment(segment.replaceAll("/?([^/]*)/?", "$1"));
                    }
                }
            }

            // Set HTTP method
            final HttpMethod httpMethod;
            final String contentType;
            final String[] headers;
            if (method.isAnnotationPresent(Get.class)) {
                final Get get = method.getAnnotation(Get.class);
                httpMethod = HttpMethod.GET;
                contentType = null;
                headers = get.headers();
            } else if (method.isAnnotationPresent(Post.class)) {
                final Post post = method.getAnnotation(Post.class); 
                httpMethod = HttpMethod.POST;
                contentType = post.contentType();
                headers = post.headers();
            } else if (method.isAnnotationPresent(Put.class)) {
                final Put put = method.getAnnotation(Put.class);
                httpMethod = HttpMethod.PUT;
                contentType = put.contentType();
                headers = put.headers();
            } else if (method.isAnnotationPresent(Patch.class)) {
                final Patch patch = method.getAnnotation(Patch.class);
                httpMethod = HttpMethod.PATCH;
                contentType = patch.contentType();
                headers = patch.headers();
            } else if (method.isAnnotationPresent(Delete.class)) {
                final Delete delete = method.getAnnotation(Delete.class);
                httpMethod = HttpMethod.DELETE;
                contentType = delete.contentType();
                headers = delete.headers();
            } else if (method.isAnnotationPresent(Head.class)) {
                final Head head = method.getAnnotation(Head.class);
                httpMethod = HttpMethod.HEAD;
                contentType = null;
                headers = head.headers();
            } else {
                httpMethod = HttpMethod.GET;
                contentType = null;
                headers = new String[0];
            }

            // Add additional headers
            for (final String header : headers) {
                addHeaders(HttpHeaders.parse(header));
            }

            final Serialization serialization = method.getAnnotation(Serialization.class);
            final Deserialization deserialization = method.getAnnotation(Deserialization.class);

            // Set HTTP entity
            final Type type = callback != null ? TypeResolver.getGenericTypeParameter(callback) : method.getReturnType();
            final Class<? extends Serializer> classOfSerializer = null != serialization ? serialization.value() : FormSerializer.class;
            final Class<? extends Deserializer> classOfDeserializer = null != deserialization ? deserialization.value() : null;

            if (null != classOfSerializer) {
                try {
                    final Constructor<? extends Serializer> ctor = classOfSerializer.getConstructor(Type.class);
                    ctor.setAccessible(true);
                    this.serializer = ctor.newInstance(new GenericType<Map<String, Object>>(){}.getType());
                } catch (final Exception e) {
                    try {
                        this.serializer = classOfSerializer.newInstance();
                    } catch (final Exception cause) {
                        throw new IllegalArgumentException(cause);
                    }
                }

                final Map<String, Object> serializable = new LinkedHashMap<String, Object>();
                if (!bodyParams.isEmpty()) {
                    for (final RpcRequest.Parameter parameter : inflate(bodyParams)) {
                        if (serializable.containsKey(parameter.getName())) {
                            final Object value = serializable.get(parameter.getName());
                            final List<Object> values = value instanceof List ? (List<Object>) value : new ArrayList<Object>(Arrays.asList(value));
                            values.add(parameter.getValue());
                            serializable.put(parameter.getName(), values);
                        } else {
                            serializable.put(parameter.getName(), parameter.getValue());
                        }
                    }
                }

                if (httpMethod.permitsRequestBody() && serializable.size() > 0) {
                    setMethod(httpMethod, new HttpBody() {
                        @Override
                        public MimeType getContentType() {
                            if (null != contentType && contentType.trim().length() > 0) {
                                return MimeType.parse(contentType);
                            }

                            return null;
                        }

                        @Override
                        public InputStream getContent() throws IOException {
                            return serializer.serialize(serializable);
                        }
                    });
                } else {
                    setMethod(httpMethod, null);
                }
            }

            if (null == type || void.class.equals(type) || Void.class.equals(type)) {
                this.deserializer = new NullDeserializer();
            } else if (null != classOfDeserializer) {
                try {
                    final Constructor<? extends Deserializer> ctor = classOfDeserializer.getConstructor(Type.class);
                    this.deserializer = ctor.newInstance(type);
                } catch (final Exception e) {
                    try {
                        this.deserializer = classOfDeserializer.newInstance();
                    } catch (final Exception cause) {
                        throw new IllegalArgumentException(cause);
                    }
                }
            }

            this.url = uriBuilder.build().toString();

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

        /**
         * Inflate RPC request parameters if necessary.
         * 
         * @param parameters
         *            The RPC request parameters
         * @return inflated parameters
         */
        static final List<RpcRequest.Parameter> inflate(final List<RpcRequest.Parameter> parameters) {
            final RpcRequest.Parameter first = parameters.isEmpty() ? null : parameters.iterator().next();

            if (null == first || parameters.size() > 1 || (null != first.getName() && first.getName().trim().length() > 0)) {
                return parameters;
            }

            final Object firstValue = first.getValue();
            final List<RpcRequest.Parameter> params = new ArrayList<RpcRequest.Parameter>();
            final Map<?, ?> map = firstValue instanceof Map<?, ?> ? (Map<?, ?>) firstValue : Introspector.properties(firstValue);

            for (final Map.Entry<?, ?> property : map.entrySet()) {
                params.add(new SimpleParameter(String.valueOf(property.getKey()), property.getValue()));
            }

            return params;
        }

    }

    static final class SimpleParameter implements RpcRequest.Parameter {

        final String mName;

        final Object mValue;

        public SimpleParameter(final String name, final Object value) {
            this.mName = name;
            this.mValue = value;
        }

        @Override
        public String getName() {
            return this.mName;
        }

        @Override
        public Object getValue() {
            return this.mValue;
        }

    }

}
