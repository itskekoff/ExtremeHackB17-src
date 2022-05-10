package io.netty.handler.codec.http;

import io.netty.handler.codec.DateFormatter;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieUtil;
import io.netty.handler.codec.http.DefaultCookie;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Deprecated
public final class CookieDecoder {
    private final InternalLogger logger = InternalLoggerFactory.getInstance(this.getClass());
    private static final CookieDecoder STRICT = new CookieDecoder(true);
    private static final CookieDecoder LAX = new CookieDecoder(false);
    private static final String COMMENT = "Comment";
    private static final String COMMENTURL = "CommentURL";
    private static final String DISCARD = "Discard";
    private static final String PORT = "Port";
    private static final String VERSION = "Version";
    private final boolean strict;

    public static Set<Cookie> decode(String header) {
        return CookieDecoder.decode(header, true);
    }

    public static Set<Cookie> decode(String header, boolean strict) {
        return (strict ? STRICT : LAX).doDecode(header);
    }

    private Set<Cookie> doDecode(String header) {
        int i2;
        ArrayList<String> names = new ArrayList<String>(8);
        ArrayList<String> values = new ArrayList<String>(8);
        CookieDecoder.extractKeyValuePairs(header, names, values);
        if (names.isEmpty()) {
            return Collections.emptySet();
        }
        int version = 0;
        if (((String)names.get(0)).equalsIgnoreCase(VERSION)) {
            try {
                version = Integer.parseInt((String)values.get(0));
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
            i2 = 1;
        } else {
            i2 = 0;
        }
        if (names.size() <= i2) {
            return Collections.emptySet();
        }
        TreeSet<Cookie> cookies = new TreeSet<Cookie>();
        while (i2 < names.size()) {
            DefaultCookie c2;
            String name = (String)names.get(i2);
            String value = (String)values.get(i2);
            if (value == null) {
                value = "";
            }
            if ((c2 = this.initCookie(name, value)) == null) break;
            boolean discard = false;
            boolean secure = false;
            boolean httpOnly = false;
            String comment = null;
            String commentURL = null;
            String domain = null;
            String path = null;
            long maxAge = Long.MIN_VALUE;
            ArrayList<Integer> ports = new ArrayList<Integer>(2);
            int j2 = i2 + 1;
            while (j2 < names.size()) {
                name = (String)names.get(j2);
                value = (String)values.get(j2);
                if (DISCARD.equalsIgnoreCase(name)) {
                    discard = true;
                } else if ("Secure".equalsIgnoreCase(name)) {
                    secure = true;
                } else if ("HTTPOnly".equalsIgnoreCase(name)) {
                    httpOnly = true;
                } else if (COMMENT.equalsIgnoreCase(name)) {
                    comment = value;
                } else if (COMMENTURL.equalsIgnoreCase(name)) {
                    commentURL = value;
                } else if ("Domain".equalsIgnoreCase(name)) {
                    domain = value;
                } else if ("Path".equalsIgnoreCase(name)) {
                    path = value;
                } else if ("Expires".equalsIgnoreCase(name)) {
                    Date date = DateFormatter.parseHttpDate(value);
                    if (date != null) {
                        long maxAgeMillis = date.getTime() - System.currentTimeMillis();
                        maxAge = maxAgeMillis / 1000L + (long)(maxAgeMillis % 1000L != 0L ? 1 : 0);
                    }
                } else if ("Max-Age".equalsIgnoreCase(name)) {
                    maxAge = Integer.parseInt(value);
                } else if (VERSION.equalsIgnoreCase(name)) {
                    version = Integer.parseInt(value);
                } else {
                    String[] portList;
                    if (!PORT.equalsIgnoreCase(name)) break;
                    for (String s1 : portList = value.split(",")) {
                        try {
                            ports.add(Integer.valueOf(s1));
                        }
                        catch (NumberFormatException numberFormatException) {
                            // empty catch block
                        }
                    }
                }
                ++j2;
                ++i2;
            }
            c2.setVersion(version);
            c2.setMaxAge(maxAge);
            c2.setPath(path);
            c2.setDomain(domain);
            c2.setSecure(secure);
            c2.setHttpOnly(httpOnly);
            if (version > 0) {
                c2.setComment(comment);
            }
            if (version > 1) {
                c2.setCommentUrl(commentURL);
                c2.setPorts(ports);
                c2.setDiscard(discard);
            }
            cookies.add(c2);
            ++i2;
        }
        return cookies;
    }

    private static void extractKeyValuePairs(String header, List<String> names, List<String> values) {
        int headerLen = header.length();
        int i2 = 0;
        block10: while (i2 != headerLen) {
            switch (header.charAt(i2)) {
                case '\t': 
                case '\n': 
                case '\u000b': 
                case '\f': 
                case '\r': 
                case ' ': 
                case ',': 
                case ';': {
                    ++i2;
                    continue block10;
                }
            }
            while (i2 != headerLen) {
                String value;
                String name;
                if (header.charAt(i2) == '$') {
                    ++i2;
                    continue;
                }
                if (i2 == headerLen) {
                    name = null;
                    value = null;
                } else {
                    int newNameStart = i2;
                    block12: while (true) {
                        switch (header.charAt(i2)) {
                            case ';': {
                                name = header.substring(newNameStart, i2);
                                value = null;
                                break block12;
                            }
                            case '=': {
                                name = header.substring(newNameStart, i2);
                                if (++i2 == headerLen) {
                                    value = "";
                                    break block12;
                                }
                                int newValueStart = i2;
                                char c2 = header.charAt(i2);
                                if (c2 == '\"' || c2 == '\'') {
                                    StringBuilder newValueBuf = new StringBuilder(header.length() - i2);
                                    char q2 = c2;
                                    boolean hadBackslash = false;
                                    ++i2;
                                    block13: while (true) {
                                        if (i2 == headerLen) {
                                            value = newValueBuf.toString();
                                            break block12;
                                        }
                                        if (hadBackslash) {
                                            hadBackslash = false;
                                            c2 = header.charAt(i2++);
                                            switch (c2) {
                                                case '\"': 
                                                case '\'': 
                                                case '\\': {
                                                    newValueBuf.setCharAt(newValueBuf.length() - 1, c2);
                                                    continue block13;
                                                }
                                            }
                                            newValueBuf.append(c2);
                                            continue;
                                        }
                                        if ((c2 = header.charAt(i2++)) == q2) {
                                            value = newValueBuf.toString();
                                            break block12;
                                        }
                                        newValueBuf.append(c2);
                                        if (c2 != '\\') continue;
                                        hadBackslash = true;
                                    }
                                }
                                int semiPos = header.indexOf(59, i2);
                                if (semiPos > 0) {
                                    value = header.substring(newValueStart, semiPos);
                                    i2 = semiPos;
                                    break block12;
                                }
                                value = header.substring(newValueStart);
                                i2 = headerLen;
                                break block12;
                            }
                            default: {
                                if (++i2 != headerLen) continue block12;
                                name = header.substring(newNameStart);
                                value = null;
                                break block12;
                            }
                        }
                        break;
                    }
                }
                names.add(name);
                values.add(value);
                continue block10;
            }
            break block10;
        }
    }

    private CookieDecoder(boolean strict) {
        this.strict = strict;
    }

    private DefaultCookie initCookie(String name, String value) {
        boolean wrap;
        int invalidOctetPos;
        if (name == null || name.length() == 0) {
            this.logger.debug("Skipping cookie with null name");
            return null;
        }
        if (value == null) {
            this.logger.debug("Skipping cookie with null value");
            return null;
        }
        CharSequence unwrappedValue = CookieUtil.unwrapValue(value);
        if (unwrappedValue == null) {
            this.logger.debug("Skipping cookie because starting quotes are not properly balanced in '{}'", (Object)unwrappedValue);
            return null;
        }
        if (this.strict && (invalidOctetPos = CookieUtil.firstInvalidCookieNameOctet(name)) >= 0) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Skipping cookie because name '{}' contains invalid char '{}'", (Object)name, (Object)Character.valueOf(name.charAt(invalidOctetPos)));
            }
            return null;
        }
        boolean bl2 = wrap = unwrappedValue.length() != value.length();
        if (this.strict && (invalidOctetPos = CookieUtil.firstInvalidCookieValueOctet(unwrappedValue)) >= 0) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Skipping cookie because value '{}' contains invalid char '{}'", (Object)unwrappedValue, (Object)Character.valueOf(unwrappedValue.charAt(invalidOctetPos)));
            }
            return null;
        }
        DefaultCookie cookie = new DefaultCookie(name, unwrappedValue.toString());
        cookie.setWrap(wrap);
        return cookie;
    }
}

