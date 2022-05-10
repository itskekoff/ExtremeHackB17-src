package io.netty.handler.codec.http.cookie;

import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.CookieUtil;
import io.netty.util.internal.ObjectUtil;

public class DefaultCookie
implements Cookie {
    private final String name;
    private String value;
    private boolean wrap;
    private String domain;
    private String path;
    private long maxAge = Long.MIN_VALUE;
    private boolean secure;
    private boolean httpOnly;

    public DefaultCookie(String name, String value) {
        name = ObjectUtil.checkNotNull(name, "name").trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("empty name");
        }
        this.name = name;
        this.setValue(value);
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String value() {
        return this.value;
    }

    @Override
    public void setValue(String value) {
        this.value = ObjectUtil.checkNotNull(value, "value");
    }

    @Override
    public boolean wrap() {
        return this.wrap;
    }

    @Override
    public void setWrap(boolean wrap) {
        this.wrap = wrap;
    }

    @Override
    public String domain() {
        return this.domain;
    }

    @Override
    public void setDomain(String domain) {
        this.domain = CookieUtil.validateAttributeValue("domain", domain);
    }

    @Override
    public String path() {
        return this.path;
    }

    @Override
    public void setPath(String path) {
        this.path = CookieUtil.validateAttributeValue("path", path);
    }

    @Override
    public long maxAge() {
        return this.maxAge;
    }

    @Override
    public void setMaxAge(long maxAge) {
        this.maxAge = maxAge;
    }

    @Override
    public boolean isSecure() {
        return this.secure;
    }

    @Override
    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    @Override
    public boolean isHttpOnly() {
        return this.httpOnly;
    }

    @Override
    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    public int hashCode() {
        return this.name().hashCode();
    }

    public boolean equals(Object o2) {
        if (this == o2) {
            return true;
        }
        if (!(o2 instanceof Cookie)) {
            return false;
        }
        Cookie that = (Cookie)o2;
        if (!this.name().equals(that.name())) {
            return false;
        }
        if (this.path() == null) {
            if (that.path() != null) {
                return false;
            }
        } else {
            if (that.path() == null) {
                return false;
            }
            if (!this.path().equals(that.path())) {
                return false;
            }
        }
        if (this.domain() == null) {
            return that.domain() == null;
        }
        return this.domain().equalsIgnoreCase(that.domain());
    }

    @Override
    public int compareTo(Cookie c2) {
        int v2 = this.name().compareTo(c2.name());
        if (v2 != 0) {
            return v2;
        }
        if (this.path() == null) {
            if (c2.path() != null) {
                return -1;
            }
        } else {
            if (c2.path() == null) {
                return 1;
            }
            v2 = this.path().compareTo(c2.path());
            if (v2 != 0) {
                return v2;
            }
        }
        if (this.domain() == null) {
            if (c2.domain() != null) {
                return -1;
            }
        } else {
            if (c2.domain() == null) {
                return 1;
            }
            v2 = this.domain().compareToIgnoreCase(c2.domain());
            return v2;
        }
        return 0;
    }

    @Deprecated
    protected String validateValue(String name, String value) {
        return CookieUtil.validateAttributeValue(name, value);
    }

    public String toString() {
        StringBuilder buf2 = CookieUtil.stringBuilder().append(this.name()).append('=').append(this.value());
        if (this.domain() != null) {
            buf2.append(", domain=").append(this.domain());
        }
        if (this.path() != null) {
            buf2.append(", path=").append(this.path());
        }
        if (this.maxAge() >= 0L) {
            buf2.append(", maxAge=").append(this.maxAge()).append('s');
        }
        if (this.isSecure()) {
            buf2.append(", secure");
        }
        if (this.isHttpOnly()) {
            buf2.append(", HTTPOnly");
        }
        return buf2.toString();
    }
}

