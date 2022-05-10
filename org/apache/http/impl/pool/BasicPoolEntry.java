package org.apache.http.impl.pool;

import java.io.IOException;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpHost;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.pool.PoolEntry;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@ThreadSafe
public class BasicPoolEntry
extends PoolEntry<HttpHost, HttpClientConnection> {
    public BasicPoolEntry(String id2, HttpHost route, HttpClientConnection conn) {
        super(id2, route, conn);
    }

    @Override
    public void close() {
        try {
            ((HttpClientConnection)this.getConnection()).close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    @Override
    public boolean isClosed() {
        return !((HttpClientConnection)this.getConnection()).isOpen();
    }
}

