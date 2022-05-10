package org.apache.http.protocol;

import java.io.IOException;
import java.util.List;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestInterceptorList;
import org.apache.http.protocol.HttpResponseInterceptorList;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@ThreadSafe
public final class ImmutableHttpProcessor
implements HttpProcessor {
    private final HttpRequestInterceptor[] requestInterceptors;
    private final HttpResponseInterceptor[] responseInterceptors;

    public ImmutableHttpProcessor(HttpRequestInterceptor[] requestInterceptors, HttpResponseInterceptor[] responseInterceptors) {
        int l2;
        if (requestInterceptors != null) {
            l2 = requestInterceptors.length;
            this.requestInterceptors = new HttpRequestInterceptor[l2];
            System.arraycopy(requestInterceptors, 0, this.requestInterceptors, 0, l2);
        } else {
            this.requestInterceptors = new HttpRequestInterceptor[0];
        }
        if (responseInterceptors != null) {
            l2 = responseInterceptors.length;
            this.responseInterceptors = new HttpResponseInterceptor[l2];
            System.arraycopy(responseInterceptors, 0, this.responseInterceptors, 0, l2);
        } else {
            this.responseInterceptors = new HttpResponseInterceptor[0];
        }
    }

    public ImmutableHttpProcessor(List<HttpRequestInterceptor> requestInterceptors, List<HttpResponseInterceptor> responseInterceptors) {
        int l2;
        if (requestInterceptors != null) {
            l2 = requestInterceptors.size();
            this.requestInterceptors = requestInterceptors.toArray(new HttpRequestInterceptor[l2]);
        } else {
            this.requestInterceptors = new HttpRequestInterceptor[0];
        }
        if (responseInterceptors != null) {
            l2 = responseInterceptors.size();
            this.responseInterceptors = responseInterceptors.toArray(new HttpResponseInterceptor[l2]);
        } else {
            this.responseInterceptors = new HttpResponseInterceptor[0];
        }
    }

    @Deprecated
    public ImmutableHttpProcessor(HttpRequestInterceptorList requestInterceptors, HttpResponseInterceptorList responseInterceptors) {
        int i2;
        int count;
        if (requestInterceptors != null) {
            count = requestInterceptors.getRequestInterceptorCount();
            this.requestInterceptors = new HttpRequestInterceptor[count];
            for (i2 = 0; i2 < count; ++i2) {
                this.requestInterceptors[i2] = requestInterceptors.getRequestInterceptor(i2);
            }
        } else {
            this.requestInterceptors = new HttpRequestInterceptor[0];
        }
        if (responseInterceptors != null) {
            count = responseInterceptors.getResponseInterceptorCount();
            this.responseInterceptors = new HttpResponseInterceptor[count];
            for (i2 = 0; i2 < count; ++i2) {
                this.responseInterceptors[i2] = responseInterceptors.getResponseInterceptor(i2);
            }
        } else {
            this.responseInterceptors = new HttpResponseInterceptor[0];
        }
    }

    public ImmutableHttpProcessor(HttpRequestInterceptor ... requestInterceptors) {
        this(requestInterceptors, (HttpResponseInterceptor[])null);
    }

    public ImmutableHttpProcessor(HttpResponseInterceptor ... responseInterceptors) {
        this((HttpRequestInterceptor[])null, responseInterceptors);
    }

    @Override
    public void process(HttpRequest request, HttpContext context) throws IOException, HttpException {
        for (HttpRequestInterceptor requestInterceptor : this.requestInterceptors) {
            requestInterceptor.process(request, context);
        }
    }

    @Override
    public void process(HttpResponse response, HttpContext context) throws IOException, HttpException {
        for (HttpResponseInterceptor responseInterceptor : this.responseInterceptors) {
            responseInterceptor.process(response, context);
        }
    }
}

