package io.netty.resolver.dns;

import io.netty.channel.EventLoop;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.resolver.dns.DnsCache;
import io.netty.resolver.dns.DnsCacheEntry;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class DefaultDnsCache
implements DnsCache {
    private final ConcurrentMap<String, List<DnsCacheEntry>> resolveCache = PlatformDependent.newConcurrentHashMap();
    private final int minTtl;
    private final int maxTtl;
    private final int negativeTtl;

    public DefaultDnsCache() {
        this(0, Integer.MAX_VALUE, 0);
    }

    public DefaultDnsCache(int minTtl, int maxTtl, int negativeTtl) {
        this.minTtl = ObjectUtil.checkPositiveOrZero(minTtl, "minTtl");
        this.maxTtl = ObjectUtil.checkPositiveOrZero(maxTtl, "maxTtl");
        if (minTtl > maxTtl) {
            throw new IllegalArgumentException("minTtl: " + minTtl + ", maxTtl: " + maxTtl + " (expected: 0 <= minTtl <= maxTtl)");
        }
        this.negativeTtl = ObjectUtil.checkPositiveOrZero(negativeTtl, "negativeTtl");
    }

    public int minTtl() {
        return this.minTtl;
    }

    public int maxTtl() {
        return this.maxTtl;
    }

    public int negativeTtl() {
        return this.negativeTtl;
    }

    @Override
    public void clear() {
        Iterator i2 = this.resolveCache.entrySet().iterator();
        while (i2.hasNext()) {
            Map.Entry e2 = i2.next();
            i2.remove();
            DefaultDnsCache.cancelExpiration((List)e2.getValue());
        }
    }

    @Override
    public boolean clear(String hostname) {
        ObjectUtil.checkNotNull(hostname, "hostname");
        boolean removed = false;
        Iterator i2 = this.resolveCache.entrySet().iterator();
        while (i2.hasNext()) {
            Map.Entry e2 = i2.next();
            if (!((String)e2.getKey()).equals(hostname)) continue;
            i2.remove();
            DefaultDnsCache.cancelExpiration((List)e2.getValue());
            removed = true;
        }
        return removed;
    }

    private static boolean emptyAdditionals(DnsRecord[] additionals) {
        return additionals == null || additionals.length == 0;
    }

    @Override
    public List<DnsCacheEntry> get(String hostname, DnsRecord[] additionals) {
        ObjectUtil.checkNotNull(hostname, "hostname");
        if (!DefaultDnsCache.emptyAdditionals(additionals)) {
            return null;
        }
        return (List)this.resolveCache.get(hostname);
    }

    private List<DnsCacheEntry> cachedEntries(String hostname) {
        ArrayList newEntries;
        ArrayList oldEntries = (ArrayList)this.resolveCache.get(hostname);
        ArrayList entries = oldEntries == null ? ((oldEntries = (List)this.resolveCache.putIfAbsent(hostname, newEntries = new ArrayList(8))) != null ? oldEntries : newEntries) : oldEntries;
        return entries;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void cache(String hostname, DnsRecord[] additionals, InetAddress address, long originalTtl, EventLoop loop) {
        ObjectUtil.checkNotNull(hostname, "hostname");
        ObjectUtil.checkNotNull(address, "address");
        ObjectUtil.checkNotNull(loop, "loop");
        if (this.maxTtl == 0 || !DefaultDnsCache.emptyAdditionals(additionals)) {
            return;
        }
        int ttl = Math.max(this.minTtl, (int)Math.min((long)this.maxTtl, originalTtl));
        List<DnsCacheEntry> entries = this.cachedEntries(hostname);
        DnsCacheEntry e2 = new DnsCacheEntry(hostname, address);
        List<DnsCacheEntry> list = entries;
        synchronized (list) {
            DnsCacheEntry firstEntry;
            if (!entries.isEmpty() && (firstEntry = entries.get(0)).cause() != null) {
                assert (entries.size() == 1);
                firstEntry.cancelExpiration();
                entries.clear();
            }
            entries.add(e2);
        }
        this.scheduleCacheExpiration(entries, e2, ttl, loop);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void cache(String hostname, DnsRecord[] additionals, Throwable cause, EventLoop loop) {
        ObjectUtil.checkNotNull(hostname, "hostname");
        ObjectUtil.checkNotNull(cause, "cause");
        ObjectUtil.checkNotNull(loop, "loop");
        if (this.negativeTtl == 0 || !DefaultDnsCache.emptyAdditionals(additionals)) {
            return;
        }
        List<DnsCacheEntry> entries = this.cachedEntries(hostname);
        DnsCacheEntry e2 = new DnsCacheEntry(hostname, cause);
        List<DnsCacheEntry> list = entries;
        synchronized (list) {
            int numEntries = entries.size();
            for (int i2 = 0; i2 < numEntries; ++i2) {
                entries.get(i2).cancelExpiration();
            }
            entries.clear();
            entries.add(e2);
        }
        this.scheduleCacheExpiration(entries, e2, this.negativeTtl, loop);
    }

    private static void cancelExpiration(List<DnsCacheEntry> entries) {
        int numEntries = entries.size();
        for (int i2 = 0; i2 < numEntries; ++i2) {
            entries.get(i2).cancelExpiration();
        }
    }

    private void scheduleCacheExpiration(final List<DnsCacheEntry> entries, final DnsCacheEntry e2, int ttl, EventLoop loop) {
        e2.scheduleExpiration(loop, new Runnable(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                List list = entries;
                synchronized (list) {
                    entries.remove(e2);
                    if (entries.isEmpty()) {
                        DefaultDnsCache.this.resolveCache.remove(e2.hostname());
                    }
                }
            }
        }, ttl, TimeUnit.SECONDS);
    }

    public String toString() {
        return "DefaultDnsCache(minTtl=" + this.minTtl + ", maxTtl=" + this.maxTtl + ", negativeTtl=" + this.negativeTtl + ", cached resolved hostname=" + this.resolveCache.size() + ")";
    }
}

