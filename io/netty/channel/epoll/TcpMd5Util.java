package io.netty.channel.epoll;

import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.Native;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

final class TcpMd5Util {
    static Collection<InetAddress> newTcpMd5Sigs(AbstractEpollChannel channel, Collection<InetAddress> current, Map<InetAddress, byte[]> newKeys) throws IOException {
        ObjectUtil.checkNotNull(channel, "channel");
        ObjectUtil.checkNotNull(current, "current");
        ObjectUtil.checkNotNull(newKeys, "newKeys");
        for (Map.Entry<InetAddress, byte[]> e2 : newKeys.entrySet()) {
            byte[] key = e2.getValue();
            if (e2.getKey() == null) {
                throw new IllegalArgumentException("newKeys contains an entry with null address: " + newKeys);
            }
            if (key == null) {
                throw new NullPointerException("newKeys[" + e2.getKey() + ']');
            }
            if (key.length == 0) {
                throw new IllegalArgumentException("newKeys[" + e2.getKey() + "] has an empty key.");
            }
            if (key.length <= Native.TCP_MD5SIG_MAXKEYLEN) continue;
            throw new IllegalArgumentException("newKeys[" + e2.getKey() + "] has a key with invalid length; should not exceed the maximum length (" + Native.TCP_MD5SIG_MAXKEYLEN + ')');
        }
        for (InetAddress addr : current) {
            if (newKeys.containsKey(addr)) continue;
            Native.setTcpMd5Sig(channel.fd().intValue(), addr, null);
        }
        if (newKeys.isEmpty()) {
            return Collections.emptySet();
        }
        ArrayList<InetAddress> addresses = new ArrayList<InetAddress>(newKeys.size());
        for (Map.Entry<InetAddress, byte[]> e3 : newKeys.entrySet()) {
            Native.setTcpMd5Sig(channel.fd().intValue(), e3.getKey(), e3.getValue());
            addresses.add(e3.getKey());
        }
        return addresses;
    }

    private TcpMd5Util() {
    }
}

