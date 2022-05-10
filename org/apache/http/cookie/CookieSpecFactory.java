package org.apache.http.cookie;

import org.apache.http.cookie.CookieSpec;
import org.apache.http.params.HttpParams;

@Deprecated
public interface CookieSpecFactory {
    public CookieSpec newInstance(HttpParams var1);
}

