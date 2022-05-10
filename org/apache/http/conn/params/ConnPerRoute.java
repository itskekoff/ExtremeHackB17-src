package org.apache.http.conn.params;

import org.apache.http.conn.routing.HttpRoute;

@Deprecated
public interface ConnPerRoute {
    public int getMaxForRoute(HttpRoute var1);
}

