package org.apache.http.client.protocol;

import java.io.IOException;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.annotation.Immutable;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Immutable
public class ResponseProcessCookies
implements HttpResponseInterceptor {
    private final Log log = LogFactory.getLog(this.getClass());

    public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
        Args.notNull(response, "HTTP request");
        Args.notNull(context, "HTTP context");
        HttpClientContext clientContext = HttpClientContext.adapt(context);
        CookieSpec cookieSpec = clientContext.getCookieSpec();
        if (cookieSpec == null) {
            this.log.debug("Cookie spec not specified in HTTP context");
            return;
        }
        CookieStore cookieStore = clientContext.getCookieStore();
        if (cookieStore == null) {
            this.log.debug("Cookie store not specified in HTTP context");
            return;
        }
        CookieOrigin cookieOrigin = clientContext.getCookieOrigin();
        if (cookieOrigin == null) {
            this.log.debug("Cookie origin not specified in HTTP context");
            return;
        }
        HeaderIterator it2 = response.headerIterator("Set-Cookie");
        this.processCookies(it2, cookieSpec, cookieOrigin, cookieStore);
        if (cookieSpec.getVersion() > 0) {
            it2 = response.headerIterator("Set-Cookie2");
            this.processCookies(it2, cookieSpec, cookieOrigin, cookieStore);
        }
    }

    private void processCookies(HeaderIterator iterator, CookieSpec cookieSpec, CookieOrigin cookieOrigin, CookieStore cookieStore) {
        while (iterator.hasNext()) {
            Header header = iterator.nextHeader();
            try {
                List<Cookie> cookies = cookieSpec.parse(header, cookieOrigin);
                for (Cookie cookie : cookies) {
                    try {
                        cookieSpec.validate(cookie, cookieOrigin);
                        cookieStore.addCookie(cookie);
                        if (!this.log.isDebugEnabled()) continue;
                        this.log.debug("Cookie accepted [" + ResponseProcessCookies.formatCooke(cookie) + "]");
                    }
                    catch (MalformedCookieException ex2) {
                        if (!this.log.isWarnEnabled()) continue;
                        this.log.warn("Cookie rejected [" + ResponseProcessCookies.formatCooke(cookie) + "] " + ex2.getMessage());
                    }
                }
            }
            catch (MalformedCookieException ex3) {
                if (!this.log.isWarnEnabled()) continue;
                this.log.warn("Invalid cookie header: \"" + header + "\". " + ex3.getMessage());
            }
        }
    }

    private static String formatCooke(Cookie cookie) {
        StringBuilder buf2 = new StringBuilder();
        buf2.append(cookie.getName());
        buf2.append("=\"");
        String v2 = cookie.getValue();
        if (v2.length() > 100) {
            v2 = v2.substring(0, 100) + "...";
        }
        buf2.append(v2);
        buf2.append("\"");
        buf2.append(", version:");
        buf2.append(Integer.toString(cookie.getVersion()));
        buf2.append(", domain:");
        buf2.append(cookie.getDomain());
        buf2.append(", path:");
        buf2.append(cookie.getPath());
        buf2.append(", expiry:");
        buf2.append(cookie.getExpiryDate());
        return buf2.toString();
    }
}

