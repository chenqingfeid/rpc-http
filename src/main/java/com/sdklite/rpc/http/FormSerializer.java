package com.sdklite.rpc.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.sdklite.io.AbstractSerializer;
import com.sdklite.util.Introspector;

/**
 * This class is used for form serialization
 * 
 * @author johnsonlee
 *
 */
public final class FormSerializer extends AbstractSerializer<Object> {

    @Override
    public InputStream serialize(final Object o) throws IOException {
        final StringBuilder form = new StringBuilder();
        final Map<?, ?> params = o instanceof Map ? (Map<?, ?>) o : Introspector.properties(o);

        for (final Iterator<?> i = params.entrySet().iterator(); i.hasNext();) {
            final Map.Entry<?, ?> param = (Entry<?, ?>) i.next();
            if (null == param.getKey() || null == param.getValue()) {
                continue;
            }

            final String paramName = URLEncoder.encode(String.valueOf(param.getKey()), "UTF-8");
            final Object paramValue = param.getValue();

            if (paramValue instanceof Collection || paramValue.getClass().isArray()) {
                final int n = Array.getLength(paramValue);

                if (n > 0) {
                    form.append(paramName).append('=').append(URLEncoder.encode(String.valueOf(Array.get(paramValue, 0)), "UTF-8"));

                    for (int j = 1; j < n; j++) {
                        form.append('&').append(paramName).append('=').append(URLEncoder.encode(String.valueOf(Array.get(paramValue, j)), "UTF-8"));
                    }
                }
            } else {
                form.append(paramName).append("=").append(URLEncoder.encode(String.valueOf(paramValue), "UTF-8"));
            }

            if (i.hasNext()) {
                form.append("&");
            }
        }

        return new ByteArrayInputStream(form.toString().getBytes());
    }

}
