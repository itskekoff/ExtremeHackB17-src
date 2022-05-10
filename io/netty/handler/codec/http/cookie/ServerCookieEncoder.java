package io.netty.handler.codec.http.cookie;

import io.netty.handler.codec.DateFormatter;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.CookieEncoder;
import io.netty.handler.codec.http.cookie.CookieUtil;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class ServerCookieEncoder
extends CookieEncoder {
    public static final ServerCookieEncoder STRICT = new ServerCookieEncoder(true);
    public static final ServerCookieEncoder LAX = new ServerCookieEncoder(false);

    private ServerCookieEncoder(boolean strict) {
        super(strict);
    }

    public String encode(String name, String value) {
        return this.encode((Cookie)new DefaultCookie(name, value));
    }

    public String encode(Cookie cookie) {
        String name = ObjectUtil.checkNotNull(cookie, "cookie").name();
        String value = cookie.value() != null ? cookie.value() : "";
        this.validateCookie(name, value);
        StringBuilder buf2 = CookieUtil.stringBuilder();
        if (cookie.wrap()) {
            CookieUtil.addQuoted(buf2, name, value);
        } else {
            CookieUtil.add(buf2, name, value);
        }
        if (cookie.maxAge() != Long.MIN_VALUE) {
            CookieUtil.add(buf2, "Max-Age", cookie.maxAge());
            Date expires = new Date(cookie.maxAge() * 1000L + System.currentTimeMillis());
            buf2.append("Expires");
            buf2.append('=');
            DateFormatter.append(expires, buf2);
            buf2.append(';');
            buf2.append(' ');
        }
        if (cookie.path() != null) {
            CookieUtil.add(buf2, "Path", cookie.path());
        }
        if (cookie.domain() != null) {
            CookieUtil.add(buf2, "Domain", cookie.domain());
        }
        if (cookie.isSecure()) {
            CookieUtil.add(buf2, "Secure");
        }
        if (cookie.isHttpOnly()) {
            CookieUtil.add(buf2, "HTTPOnly");
        }
        return CookieUtil.stripTrailingSeparator(buf2);
    }

    private static List<String> dedup(List<String> encoded, Map<String, Integer> nameToLastIndex) {
        boolean[] isLastInstance = new boolean[encoded.size()];
        for (int idx : nameToLastIndex.values()) {
            isLastInstance[idx] = true;
        }
        ArrayList<String> dedupd = new ArrayList<String>(nameToLastIndex.size());
        int n2 = encoded.size();
        for (int i2 = 0; i2 < n2; ++i2) {
            if (!isLastInstance[i2]) continue;
            dedupd.add(encoded.get(i2));
        }
        return dedupd;
    }

    public List<String> encode(Cookie ... cookies) {
        if (ObjectUtil.checkNotNull(cookies, "cookies").length == 0) {
            return Collections.emptyList();
        }
        ArrayList<String> encoded = new ArrayList<String>(cookies.length);
        HashMap<String, Integer> nameToIndex = this.strict && cookies.length > 1 ? new HashMap<String, Integer>() : null;
        boolean hasDupdName = false;
        for (int i2 = 0; i2 < cookies.length; ++i2) {
            Cookie c2 = cookies[i2];
            encoded.add(this.encode(c2));
            if (nameToIndex == null) continue;
            hasDupdName |= nameToIndex.put(c2.name(), i2) != null;
        }
        return hasDupdName ? ServerCookieEncoder.dedup(encoded, nameToIndex) : encoded;
    }

    public List<String> encode(Collection<? extends Cookie> cookies) {
        if (ObjectUtil.checkNotNull(cookies, "cookies").isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<String> encoded = new ArrayList<String>(cookies.size());
        HashMap<String, Integer> nameToIndex = this.strict && cookies.size() > 1 ? new HashMap<String, Integer>() : null;
        int i2 = 0;
        boolean hasDupdName = false;
        for (Cookie cookie : cookies) {
            encoded.add(this.encode(cookie));
            if (nameToIndex == null) continue;
            hasDupdName |= nameToIndex.put(cookie.name(), i2++) != null;
        }
        return hasDupdName ? ServerCookieEncoder.dedup(encoded, nameToIndex) : encoded;
    }

    public List<String> encode(Iterable<? extends Cookie> cookies) {
        boolean hasDupdName;
        Iterator<? extends Cookie> cookiesIt = ObjectUtil.checkNotNull(cookies, "cookies").iterator();
        if (!cookiesIt.hasNext()) {
            return Collections.emptyList();
        }
        ArrayList<String> encoded = new ArrayList<String>();
        Cookie firstCookie = cookiesIt.next();
        HashMap<String, Integer> nameToIndex = this.strict && cookiesIt.hasNext() ? new HashMap<String, Integer>() : null;
        int i2 = 0;
        encoded.add(this.encode(firstCookie));
        boolean bl2 = hasDupdName = nameToIndex != null && nameToIndex.put(firstCookie.name(), i2++) != null;
        while (cookiesIt.hasNext()) {
            Cookie c2 = cookiesIt.next();
            encoded.add(this.encode(c2));
            if (nameToIndex == null) continue;
            hasDupdName |= nameToIndex.put(c2.name(), i2++) != null;
        }
        return hasDupdName ? ServerCookieEncoder.dedup(encoded, nameToIndex) : encoded;
    }
}

