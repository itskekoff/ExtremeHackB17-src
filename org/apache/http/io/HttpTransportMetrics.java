package org.apache.http.io;

public interface HttpTransportMetrics {
    public long getBytesTransferred();

    public void reset();
}

