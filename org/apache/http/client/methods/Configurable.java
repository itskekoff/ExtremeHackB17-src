package org.apache.http.client.methods;

import org.apache.http.client.config.RequestConfig;

public interface Configurable {
    public RequestConfig getConfig();
}

