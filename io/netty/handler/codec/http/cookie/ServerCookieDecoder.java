package io.netty.handler.codec.http.cookie;

import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.CookieDecoder;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.util.internal.ObjectUtil;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public final class ServerCookieDecoder
extends CookieDecoder {
    private static final String RFC2965_VERSION = "$Version";
    private static final String RFC2965_PATH = "$Path";
    private static final String RFC2965_DOMAIN = "$Domain";
    private static final String RFC2965_PORT = "$Port";
    public static final ServerCookieDecoder STRICT = new ServerCookieDecoder(true);
    public static final ServerCookieDecoder LAX = new ServerCookieDecoder(false);

    private ServerCookieDecoder(boolean strict) {
        super(strict);
    }

    public Set<Cookie> decode(String header) {
        int headerLen = ObjectUtil.checkNotNull(header, "header").length();
        if (headerLen == 0) {
            return Collections.emptySet();
        }
        TreeSet<Cookie> cookies = new TreeSet<Cookie>();
        int i2 = 0;
        boolean rfc2965Style = false;
        if (header.regionMatches(true, 0, RFC2965_VERSION, 0, RFC2965_VERSION.length())) {
            i2 = header.indexOf(59) + 1;
            rfc2965Style = true;
        }
        while (i2 != headerLen) {
            DefaultCookie cookie;
            int valueBegin;
            int valueEnd;
            int nameEnd;
            int nameBegin;
            block9: {
                char c2 = header.charAt(i2);
                if (c2 == '\t' || c2 == '\n' || c2 == '\u000b' || c2 == '\f' || c2 == '\r' || c2 == ' ' || c2 == ',' || c2 == ';') {
                    ++i2;
                    continue;
                }
                nameBegin = i2;
                do {
                    char curChar;
                    if ((curChar = header.charAt(i2)) == ';') {
                        nameEnd = i2;
                        valueEnd = -1;
                        valueBegin = -1;
                    } else {
                        if (curChar != '=') continue;
                        nameEnd = i2++;
                        if (i2 == headerLen) {
                            valueEnd = 0;
                            valueBegin = 0;
                        } else {
                            valueBegin = i2;
                            int semiPos = header.indexOf(59, i2);
                            i2 = semiPos > 0 ? semiPos : headerLen;
                            valueEnd = i2;
                        }
                    }
                    break block9;
                } while (++i2 != headerLen);
                nameEnd = headerLen;
                valueEnd = -1;
                valueBegin = -1;
            }
            if (rfc2965Style && (header.regionMatches(nameBegin, RFC2965_PATH, 0, RFC2965_PATH.length()) || header.regionMatches(nameBegin, RFC2965_DOMAIN, 0, RFC2965_DOMAIN.length()) || header.regionMatches(nameBegin, RFC2965_PORT, 0, RFC2965_PORT.length())) || (cookie = this.initCookie(header, nameBegin, nameEnd, valueBegin, valueEnd)) == null) continue;
            cookies.add(cookie);
        }
        return cookies;
    }
}

