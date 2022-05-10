package io.netty.channel.socket.nio;

import io.netty.channel.ChannelException;
import io.netty.channel.socket.DatagramChannelConfig;
import io.netty.channel.socket.DefaultDatagramChannelConfig;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SocketUtils;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.channels.DatagramChannel;
import java.util.Enumeration;

class NioDatagramChannelConfig
extends DefaultDatagramChannelConfig {
    private static final Object IP_MULTICAST_TTL;
    private static final Object IP_MULTICAST_IF;
    private static final Object IP_MULTICAST_LOOP;
    private static final Method GET_OPTION;
    private static final Method SET_OPTION;
    private final DatagramChannel javaChannel;

    NioDatagramChannelConfig(NioDatagramChannel channel, DatagramChannel javaChannel) {
        super(channel, javaChannel.socket());
        this.javaChannel = javaChannel;
    }

    @Override
    public int getTimeToLive() {
        return (Integer)this.getOption0(IP_MULTICAST_TTL);
    }

    @Override
    public DatagramChannelConfig setTimeToLive(int ttl) {
        this.setOption0(IP_MULTICAST_TTL, ttl);
        return this;
    }

    @Override
    public InetAddress getInterface() {
        NetworkInterface inf = this.getNetworkInterface();
        if (inf == null) {
            return null;
        }
        Enumeration<InetAddress> addresses = SocketUtils.addressesFromNetworkInterface(inf);
        if (addresses.hasMoreElements()) {
            return addresses.nextElement();
        }
        return null;
    }

    @Override
    public DatagramChannelConfig setInterface(InetAddress interfaceAddress) {
        try {
            this.setNetworkInterface(NetworkInterface.getByInetAddress(interfaceAddress));
        }
        catch (SocketException e2) {
            throw new ChannelException(e2);
        }
        return this;
    }

    @Override
    public NetworkInterface getNetworkInterface() {
        return (NetworkInterface)this.getOption0(IP_MULTICAST_IF);
    }

    @Override
    public DatagramChannelConfig setNetworkInterface(NetworkInterface networkInterface) {
        this.setOption0(IP_MULTICAST_IF, networkInterface);
        return this;
    }

    @Override
    public boolean isLoopbackModeDisabled() {
        return (Boolean)this.getOption0(IP_MULTICAST_LOOP);
    }

    @Override
    public DatagramChannelConfig setLoopbackModeDisabled(boolean loopbackModeDisabled) {
        this.setOption0(IP_MULTICAST_LOOP, loopbackModeDisabled);
        return this;
    }

    @Override
    public DatagramChannelConfig setAutoRead(boolean autoRead) {
        super.setAutoRead(autoRead);
        return this;
    }

    @Override
    protected void autoReadCleared() {
        ((NioDatagramChannel)this.channel).clearReadPending0();
    }

    private Object getOption0(Object option) {
        if (GET_OPTION == null) {
            throw new UnsupportedOperationException();
        }
        try {
            return GET_OPTION.invoke(this.javaChannel, option);
        }
        catch (Exception e2) {
            throw new ChannelException(e2);
        }
    }

    private void setOption0(Object option, Object value) {
        if (SET_OPTION == null) {
            throw new UnsupportedOperationException();
        }
        try {
            SET_OPTION.invoke(this.javaChannel, option, value);
        }
        catch (Exception e2) {
            throw new ChannelException(e2);
        }
    }

    static {
        ClassLoader classLoader = PlatformDependent.getClassLoader(DatagramChannel.class);
        Class<?> socketOptionType = null;
        try {
            socketOptionType = Class.forName("java.net.SocketOption", true, classLoader);
        }
        catch (Exception exception) {
            // empty catch block
        }
        Class<?> stdSocketOptionType = null;
        try {
            stdSocketOptionType = Class.forName("java.net.StandardSocketOptions", true, classLoader);
        }
        catch (Exception exception) {
            // empty catch block
        }
        Object ipMulticastTtl = null;
        Object ipMulticastIf = null;
        Object ipMulticastLoop = null;
        Method getOption = null;
        Method setOption = null;
        if (socketOptionType != null) {
            try {
                ipMulticastTtl = stdSocketOptionType.getDeclaredField("IP_MULTICAST_TTL").get(null);
            }
            catch (Exception e2) {
                throw new Error("cannot locate the IP_MULTICAST_TTL field", e2);
            }
            try {
                ipMulticastIf = stdSocketOptionType.getDeclaredField("IP_MULTICAST_IF").get(null);
            }
            catch (Exception e3) {
                throw new Error("cannot locate the IP_MULTICAST_IF field", e3);
            }
            try {
                ipMulticastLoop = stdSocketOptionType.getDeclaredField("IP_MULTICAST_LOOP").get(null);
            }
            catch (Exception e4) {
                throw new Error("cannot locate the IP_MULTICAST_LOOP field", e4);
            }
            Class<?> networkChannelClass = null;
            try {
                networkChannelClass = Class.forName("java.nio.channels.NetworkChannel", true, classLoader);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            if (networkChannelClass == null) {
                getOption = null;
                setOption = null;
            } else {
                try {
                    getOption = networkChannelClass.getDeclaredMethod("getOption", socketOptionType);
                }
                catch (Exception e5) {
                    throw new Error("cannot locate the getOption() method", e5);
                }
                try {
                    setOption = networkChannelClass.getDeclaredMethod("setOption", socketOptionType, Object.class);
                }
                catch (Exception e6) {
                    throw new Error("cannot locate the setOption() method", e6);
                }
            }
        }
        IP_MULTICAST_TTL = ipMulticastTtl;
        IP_MULTICAST_IF = ipMulticastIf;
        IP_MULTICAST_LOOP = ipMulticastLoop;
        GET_OPTION = getOption;
        SET_OPTION = setOption;
    }
}

