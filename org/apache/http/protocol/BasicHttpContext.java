package org.apache.http.protocol;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@ThreadSafe
public class BasicHttpContext
implements HttpContext {
    private final HttpContext parentContext;
    private final Map<String, Object> map = new ConcurrentHashMap<String, Object>();

    public BasicHttpContext() {
        this(null);
    }

    public BasicHttpContext(HttpContext parentContext) {
        this.parentContext = parentContext;
    }

    public Object getAttribute(String id2) {
        Args.notNull(id2, "Id");
        Object obj = this.map.get(id2);
        if (obj == null && this.parentContext != null) {
            obj = this.parentContext.getAttribute(id2);
        }
        return obj;
    }

    public void setAttribute(String id2, Object obj) {
        Args.notNull(id2, "Id");
        if (obj != null) {
            this.map.put(id2, obj);
        } else {
            this.map.remove(id2);
        }
    }

    public Object removeAttribute(String id2) {
        Args.notNull(id2, "Id");
        return this.map.remove(id2);
    }

    public void clear() {
        this.map.clear();
    }

    public String toString() {
        return this.map.toString();
    }
}

