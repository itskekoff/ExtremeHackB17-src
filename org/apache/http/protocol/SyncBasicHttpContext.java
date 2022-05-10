package org.apache.http.protocol;

import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

@Deprecated
public class SyncBasicHttpContext
extends BasicHttpContext {
    public SyncBasicHttpContext(HttpContext parentContext) {
        super(parentContext);
    }

    public SyncBasicHttpContext() {
    }

    public synchronized Object getAttribute(String id2) {
        return super.getAttribute(id2);
    }

    public synchronized void setAttribute(String id2, Object obj) {
        super.setAttribute(id2, obj);
    }

    public synchronized Object removeAttribute(String id2) {
        return super.removeAttribute(id2);
    }

    public synchronized void clear() {
        super.clear();
    }
}

