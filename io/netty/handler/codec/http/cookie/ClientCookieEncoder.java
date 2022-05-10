package io.netty.handler.codec.http.cookie;

import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.CookieEncoder;
import io.netty.handler.codec.http.cookie.CookieUtil;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

public final class ClientCookieEncoder
extends CookieEncoder {
    public static final ClientCookieEncoder STRICT = new ClientCookieEncoder(true);
    public static final ClientCookieEncoder LAX = new ClientCookieEncoder(false);
    private static final Comparator<Cookie> COOKIE_COMPARATOR = new Comparator<Cookie>(){

        @Override
        public int compare(Cookie c1, Cookie c2) {
            int len1;
            String path1 = c1.path();
            String path2 = c2.path();
            int len2 = path2 == null ? Integer.MAX_VALUE : path2.length();
            int diff = len2 - (len1 = path1 == null ? Integer.MAX_VALUE : path1.length());
            if (diff != 0) {
                return diff;
            }
            return -1;
        }
    };

    private ClientCookieEncoder(boolean strict) {
        super(strict);
    }

    public String encode(String name, String value) {
        return this.encode((Cookie)new DefaultCookie(name, value));
    }

    public String encode(Cookie cookie) {
        StringBuilder buf2 = CookieUtil.stringBuilder();
        this.encode(buf2, ObjectUtil.checkNotNull(cookie, "cookie"));
        return CookieUtil.stripTrailingSeparator(buf2);
    }

    public String encode(Cookie ... cookies) {
        if (ObjectUtil.checkNotNull(cookies, "cookies").length == 0) {
            return null;
        }
        StringBuilder buf2 = CookieUtil.stringBuilder();
        if (this.strict) {
            if (cookies.length == 1) {
                this.encode(buf2, cookies[0]);
            } else {
                Cookie[] cookiesSorted = Arrays.copyOf(cookies, cookies.length);
                Arrays.sort(cookiesSorted, COOKIE_COMPARATOR);
                for (Cookie c2 : cookiesSorted) {
                    this.encode(buf2, c2);
                }
            }
        } else {
            for (Cookie c3 : cookies) {
                this.encode(buf2, c3);
            }
        }
        return CookieUtil.stripTrailingSeparatorOrNull(buf2);
    }

    public String encode(Collection<? extends Cookie> cookies) {
        if (ObjectUtil.checkNotNull(cookies, "cookies").isEmpty()) {
            return null;
        }
        StringBuilder buf2 = CookieUtil.stringBuilder();
        if (this.strict) {
            if (cookies.size() == 1) {
                this.encode(buf2, cookies.iterator().next());
            } else {
                Cookie[] cookiesSorted = cookies.toArray(new Cookie[cookies.size()]);
                Arrays.sort(cookiesSorted, COOKIE_COMPARATOR);
                for (Cookie c2 : cookiesSorted) {
                    this.encode(buf2, c2);
                }
            }
        } else {
            for (Cookie cookie : cookies) {
                this.encode(buf2, cookie);
            }
        }
        return CookieUtil.stripTrailingSeparatorOrNull(buf2);
    }

    public String encode(Iterable<? extends Cookie> cookies) {
        Iterator<? extends Cookie> cookiesIt = ObjectUtil.checkNotNull(cookies, "cookies").iterator();
        if (!cookiesIt.hasNext()) {
            return null;
        }
        StringBuilder buf2 = CookieUtil.stringBuilder();
        if (this.strict) {
            Cookie firstCookie = cookiesIt.next();
            if (!cookiesIt.hasNext()) {
                this.encode(buf2, firstCookie);
            } else {
                ArrayList<Cookie> cookiesList = InternalThreadLocalMap.get().arrayList();
                cookiesList.add(firstCookie);
                while (cookiesIt.hasNext()) {
                    cookiesList.add(cookiesIt.next());
                }
                Cookie[] cookiesSorted = cookiesList.toArray(new Cookie[cookiesList.size()]);
                Arrays.sort(cookiesSorted, COOKIE_COMPARATOR);
                for (Cookie c2 : cookiesSorted) {
                    this.encode(buf2, c2);
                }
            }
        } else {
            while (cookiesIt.hasNext()) {
                this.encode(buf2, cookiesIt.next());
            }
        }
        return CookieUtil.stripTrailingSeparatorOrNull(buf2);
    }

    private void encode(StringBuilder buf2, Cookie c2) {
        String name = c2.name();
        String value = c2.value() != null ? c2.value() : "";
        this.validateCookie(name, value);
        if (c2.wrap()) {
            CookieUtil.addQuoted(buf2, name, value);
        } else {
            CookieUtil.add(buf2, name, value);
        }
    }
}

