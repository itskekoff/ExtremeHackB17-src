package org.apache.http.protocol;

import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Deprecated
public final class DefaultedHttpContext
implements HttpContext {
    private final HttpContext local;
    private final HttpContext defaults;

    public DefaultedHttpContext(HttpContext local, HttpContext defaults) {
        this.local = Args.notNull(local, "HTTP context");
        this.defaults = defaults;
    }

    public Object getAttribute(String id2) {
        Object obj = this.local.getAttribute(id2);
        if (obj == null) {
            return this.defaults.getAttribute(id2);
        }
        return obj;
    }

    public Object removeAttribute(String id2) {
        return this.local.removeAttribute(id2);
    }

    public void setAttribute(String id2, Object obj) {
        this.local.setAttribute(id2, obj);
    }

    public HttpContext getDefaults() {
        return this.defaults;
    }

    public String toString() {
        StringBuilder buf2 = new StringBuilder();
        buf2.append("[local: ").append(this.local);
        buf2.append("defaults: ").append(this.defaults);
        buf2.append("]");
        return buf2.toString();
    }
}

