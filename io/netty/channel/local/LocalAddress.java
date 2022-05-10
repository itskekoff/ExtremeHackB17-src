package io.netty.channel.local;

import io.netty.channel.Channel;
import java.net.SocketAddress;

public final class LocalAddress
extends SocketAddress
implements Comparable<LocalAddress> {
    private static final long serialVersionUID = 4644331421130916435L;
    public static final LocalAddress ANY = new LocalAddress("ANY");
    private final String id;
    private final String strVal;

    LocalAddress(Channel channel) {
        StringBuilder buf2 = new StringBuilder(16);
        buf2.append("local:E");
        buf2.append(Long.toHexString((long)channel.hashCode() & 0xFFFFFFFFL | 0x100000000L));
        buf2.setCharAt(7, ':');
        this.id = buf2.substring(6);
        this.strVal = buf2.toString();
    }

    public LocalAddress(String id2) {
        if (id2 == null) {
            throw new NullPointerException("id");
        }
        if ((id2 = id2.trim().toLowerCase()).isEmpty()) {
            throw new IllegalArgumentException("empty id");
        }
        this.id = id2;
        this.strVal = "local:" + id2;
    }

    public String id() {
        return this.id;
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public boolean equals(Object o2) {
        if (!(o2 instanceof LocalAddress)) {
            return false;
        }
        return this.id.equals(((LocalAddress)o2).id);
    }

    @Override
    public int compareTo(LocalAddress o2) {
        return this.id.compareTo(o2.id);
    }

    public String toString() {
        return this.strVal;
    }
}

